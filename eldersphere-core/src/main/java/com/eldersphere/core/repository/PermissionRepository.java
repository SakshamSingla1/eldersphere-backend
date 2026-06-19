package com.eldersphere.core.repository;

import com.eldersphere.core.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByResourceAndAction(String resource, String action);

    List<Permission> findAllByResource(String resource);

    boolean existsByResourceAndAction(String resource, String action);
}
