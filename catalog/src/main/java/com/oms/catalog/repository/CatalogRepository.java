package com.oms.catalog.repository;

import com.oms.catalog.entity.CatalogItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository extends JpaRepository<CatalogItemEntity, Integer> {
    public List<CatalogItemEntity> findAllByCategory(String category);

    @Transactional
    public void deleteAllByCategory(String category);
}
