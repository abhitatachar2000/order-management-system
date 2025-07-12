package com.oms.catalog.repository;

import com.oms.catalog.entity.CatalogItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<CatalogItemEntity, Integer> {
    //do nothing as of now.
}
