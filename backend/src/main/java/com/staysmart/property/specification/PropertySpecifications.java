package com.staysmart.property.specification;

import com.staysmart.property.dto.PropertySearchCriteria;
import com.staysmart.property.entity.AvailabilityBlock;
import com.staysmart.property.entity.Property;
import com.staysmart.property.entity.PropertyStatus;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a dynamic JPA {@link Specification} from {@link PropertySearchCriteria}. Shared by the
 * regular {@code GET /properties/search} endpoint and the AI Smart Search feature, which turns a
 * natural-language query into the same criteria object before delegating here.
 */
public final class PropertySpecifications {

    private PropertySpecifications() {
    }

    public static Specification<Property> fromCriteria(PropertySearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), PropertyStatus.ACTIVE));

            if (criteria.city() != null && !criteria.city().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + criteria.city().toLowerCase() + "%"));
            }
            if (criteria.country() != null && !criteria.country().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("country")), "%" + criteria.country().toLowerCase() + "%"));
            }
            if (criteria.guests() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxGuests"), criteria.guests()));
            }
            if (criteria.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerNight"), criteria.minPrice()));
            }
            if (criteria.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerNight"), criteria.maxPrice()));
            }
            if (criteria.propertyType() != null && !criteria.propertyType().isBlank()) {
                predicates.add(cb.equal(root.get("propertyType"), criteria.propertyType().toUpperCase()));
            }
            if (criteria.roomType() != null && !criteria.roomType().isBlank()) {
                predicates.add(cb.equal(root.get("roomType"), criteria.roomType().toUpperCase()));
            }
            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                // Match if ANY word of the keyword appears in the title/description, rather than
                // requiring the whole phrase verbatim - a free-text keyword like "cozy beach"
                // (whether typed by a guest or extracted by AI Smart Search) should still surface
                // a listing whose description only says "...walk from Palolem Beach...".
                List<Predicate> wordPredicates = new ArrayList<>();
                for (String word : criteria.keyword().toLowerCase().split("\\s+")) {
                    if (word.isBlank()) {
                        continue;
                    }
                    String like = "%" + word + "%";
                    wordPredicates.add(cb.or(
                            cb.like(cb.lower(root.get("title")), like),
                            cb.like(cb.lower(root.get("description")), like)));
                }
                if (!wordPredicates.isEmpty()) {
                    predicates.add(cb.or(wordPredicates.toArray(new Predicate[0])));
                }
            }
            if (criteria.amenityIds() != null && !criteria.amenityIds().isEmpty()) {
                predicates.add(root.join("amenities").get("id").in(criteria.amenityIds()));
                query.distinct(true);
            }
            if (criteria.checkIn() != null && criteria.checkOut() != null) {
                Subquery<Long> overlapping = query.subquery(Long.class);
                var blockRoot = overlapping.from(AvailabilityBlock.class);
                overlapping.select(blockRoot.get("property").get("id"))
                        .where(cb.and(
                                cb.equal(blockRoot.get("property"), root),
                                cb.lessThan(blockRoot.<java.time.LocalDate>get("startDate"), criteria.checkOut()),
                                cb.greaterThan(blockRoot.<java.time.LocalDate>get("endDate"), criteria.checkIn())));
                predicates.add(cb.not(cb.exists(overlapping)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Property> byHost(Long hostId) {
        return (root, query, cb) -> cb.equal(root.get("host").get("id"), hostId);
    }
}
