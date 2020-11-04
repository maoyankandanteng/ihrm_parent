package com.ihrm.common.service;

import org.springframework.data.jpa.domain.Specification;

public class BaseService<T> {

    protected Specification<T> getSpec(String companyId) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
    }
}
