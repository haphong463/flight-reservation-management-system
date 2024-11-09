package com.windev.flight_service.repository.specification;

import com.windev.flight_service.entity.Crew;
import java.util.Date;
import org.springframework.data.jpa.domain.Specification;

public class CrewSpecification {
    public static Specification<Crew> hasCodeContaining(String code) {
        return (root, query, builder) -> builder.like(root.get("code"), "%" + code + "%");
    }

    public static Specification<Crew> hasFirstNameContaining(String firstName) {
        return (root, query, builder) -> builder.like(root.get("firstName"), "%" + firstName + "%");
    }

    public static Specification<Crew> hasLastNameContaining(String lastName) {
        return (root, query, builder) -> builder.like(root.get("lastName"), "%" + lastName + "%");
    }

    public static Specification<Crew> hasRole(String role) {
        return (root, query, builder) -> builder.equal(root.get("role"), role);
    }

    public static Specification<Crew> hasLicenseNumberContaining(String licenseNumber) {
        return (root, query, builder) -> builder.like(root.get("licenseNumber"), "%" + licenseNumber + "%");
    }

    public static Specification<Crew> hasIssueDateAfterOrEqual(Date issueDate) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("issueDate"), issueDate);
    }

    public static Specification<Crew> hasExpirationDateBeforeOrEqual(Date expirationDate) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("expirationDate"), expirationDate);
    }

    public static Specification<Crew> hasStatus(String status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }

    public static Specification<Crew> hasPhoneContaining(String phone) {
        return (root, query, builder) -> builder.like(root.get("phone"), "%" + phone + "%");
    }
}
