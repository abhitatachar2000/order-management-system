package com.oms.catalog.repository;

import com.oms.catalog.entity.CatalogItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogRepository extends JpaRepository<CatalogItemEntity, Integer> {
    public List<CatalogItemEntity> findAllByCategory(String category);

    @Transactional
    public void deleteAllByCategory(String category);
}
