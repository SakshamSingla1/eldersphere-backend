package com.eldersphere.core.dao.admin;

import com.eldersphere.core.entities.Permission;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.ResourceNotFoundException;
import com.eldersphere.core.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class PermissionDao {

    private final PermissionRepository permissionRepository;

    public PermissionDao(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public Permission save(Permission permission) throws ElderSphereException {
        try {
            return permissionRepository.save(permission);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate permission resource+action: {}", e.getMessage());
            throw BadRequestException.conflict("Permission already exists for " + permission.getResource() + ":" + permission.getAction());
        } catch (Exception e) {
            log.error("Failed to save permission: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to save permission");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws ElderSphereException {
        try {
            permissionRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete permission id {}: {}", id, e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.PROCESSING_ERROR, "Failed to delete permission");
        }
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public Permission findByIdOrThrow(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Permission", id));
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public List<Permission> findAllByResource(String resource) {
        return permissionRepository.findAllByResource(resource);
    }

    public boolean existsByResourceAndAction(String resource, String action) {
        return permissionRepository.existsByResourceAndAction(resource, action);
    }
}
