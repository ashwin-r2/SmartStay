# UML Class Diagrams

## Domain model (entities)

```mermaid
classDiagram
    class BaseEntity {
        <<abstract>>
        +Long id
        +Instant createdAt
        +Instant updatedAt
    }

    class User {
        +String fullName
        +String email
        +String passwordHash
        +String phone
        +Role role
        +String avatarUrl
        +boolean enabled
        +getAuthorities() Collection~GrantedAuthority~
    }

    class Role {
        <<enumeration>>
        USER
        HOST
        ADMIN
    }

    class RefreshToken {
        +String tokenHash
        +Instant expiresAt
        +boolean revoked
    }

    class Property {
        +String title
        +String description
        +PropertyType propertyType
        +RoomType roomType
        +String city
        +String country
        +BigDecimal pricePerNight
        +BigDecimal cleaningFee
        +int maxGuests
        +BigDecimal avgRating
        +int reviewCount
        +PropertyStatus status
        +boolean aiGeneratedDescription
        +addImage(PropertyImage)
        +recalculateRating(BigDecimal, int)
    }

    class PropertyImage {
        +String url
        +int displayOrder
    }

    class Amenity {
        +String name
        +String icon
    }

    class AvailabilityBlock {
        +LocalDate startDate
        +LocalDate endDate
        +AvailabilityReason reason
        +Long bookingId
        +overlaps(LocalDate, LocalDate) boolean
    }

    class Booking {
        +LocalDate checkIn
        +LocalDate checkOut
        +int guestsCount
        +int nights
        +BigDecimal totalPrice
        +BookingStatus status
        +String cancellationReason
    }

    class BookingStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        CANCELLED
        COMPLETED
    }

    class Review {
        +short rating
        +String comment
    }

    class ReviewImage {
        +String url
        +int displayOrder
    }

    class AiUsageLog {
        +AiFeature feature
        +Integer promptTokens
        +Integer completionTokens
        +Long latencyMs
        +boolean success
    }

    class AiChatMessage {
        +String sessionId
        +ChatRole role
        +String content
    }

    BaseEntity <|-- User
    BaseEntity <|-- RefreshToken
    BaseEntity <|-- Property
    BaseEntity <|-- Booking
    BaseEntity <|-- Review

    User "1" o-- "*" RefreshToken
    User "1" o-- "*" Property : hosts
    User "1" o-- "*" Booking : guest
    User "1" o-- "*" Review : author
    User "1" -- "1" Role

    Property "1" *-- "*" PropertyImage
    Property "*" o-- "*" Amenity : property_amenities
    Property "1" o-- "*" AvailabilityBlock
    Property "1" o-- "*" Booking
    Property "1" o-- "*" Review

    Booking "1" -- "0..1" Review
    Booking "1" -- "1" BookingStatus

    Review "1" *-- "*" ReviewImage
```

## Backend layered architecture (per module)

Every module follows the same layering; `property` is shown as the representative example
— `user`, `booking`, `review`, `ai` and `admin` mirror this shape.

```mermaid
classDiagram
    class PropertyController {
        +search(criteria, pageable)
        +getById(id)
        +create(request)
        +update(id, request)
        +delete(id)
        +uploadImages(id, files)
    }

    class PropertyService {
        -PropertyRepository propertyRepository
        -AmenityRepository amenityRepository
        -AvailabilityBlockRepository availabilityBlockRepository
        -ImageStorageService imageStorageService
        +create(hostId, request) PropertyResponse
        +search(criteria, pageable) Page~PropertySummaryResponse~
        +isAvailable(propertyId, checkIn, checkOut) boolean
        +blockForBooking(propertyId, checkIn, checkOut, bookingId)
        +refreshRating(propertyId, avg, count)
    }

    class PropertyRepository {
        <<interface>>
        +findAll(Specification, Pageable) Page~Property~
    }

    class ImageStorageService {
        +store(file, subDirectory) String
    }

    class GlobalExceptionHandler {
        +handleNotFound(...)
        +handleValidation(...)
        +handleAiService(...)
    }

    PropertyController --> PropertyService : uses
    PropertyService --> PropertyRepository : uses
    PropertyService --> ImageStorageService : uses
    PropertyController ..> GlobalExceptionHandler : errors handled by
```
