package com.staysmart.user.entity;

/** Platform roles. A user can rent as a guest ({@code USER}), list properties ({@code HOST}), or
 *  administer the platform ({@code ADMIN}). Role checks are enforced via Spring Security
 *  {@code hasRole(...)} which expects the {@code ROLE_} prefix, added in {@link User#getAuthorities()}. */
public enum Role {
    USER,
    HOST,
    ADMIN
}
