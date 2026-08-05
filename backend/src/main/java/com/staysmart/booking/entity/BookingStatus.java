package com.staysmart.booking.entity;

/**
 * PENDING is reserved for a future host-approval workflow; the current flow is "instant book"
 * so new bookings go straight to CONFIRMED. A scheduled job flips CONFIRMED bookings whose
 * check-out date has passed to COMPLETED, which is what unlocks review eligibility.
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
