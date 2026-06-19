package com.eldersphere.core.repository;

import com.eldersphere.core.dto.configuration.ConfigurationListResponseDTO;
import com.eldersphere.core.dto.configuration.ConfigurationResponseDTO;
import com.eldersphere.core.entities.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    @Query("SELECT new com.eldersphere.core.dto.configuration.ConfigurationResponseDTO(c.id, c.context, c.data) " +
           "FROM Configuration c WHERE (:contexts IS NULL OR c.context IN (:contexts))")
    List<ConfigurationResponseDTO> findConfigurationByContext(@Param("contexts") List<String> contexts);

    @Query("SELECT new com.eldersphere.core.dto.configuration.ConfigurationListResponseDTO(" +
           "c.id, c.context, c.createdAt, c.updatedAt) FROM Configuration c " +
           "WHERE (:search IS NULL OR LOWER(c.context) LIKE LOWER(CONCAT('%', TRIM(:search), '%')))")
    Page<ConfigurationListResponseDTO> findAllConfigurationsByCriteria(@Param("search") String search, Pageable pageable);
}
