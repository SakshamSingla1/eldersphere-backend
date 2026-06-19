package com.eldersphere.core.dao.admin;

import com.eldersphere.core.entities.Role;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.ResourceNotFoundException;
import com.eldersphere.core.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class RoleDao {

    private final RoleRepository roleRepository;

    public RoleDao(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public Role save(Role role) throws ElderSphereException {
        try {
            return roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate role name: {}", e.getMessage());
            throw BadRequestException.conflict("Role already exists with name: " + role.getName());
        } catch (Exception e) {
            log.error("Failed to save role: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_CREATE, "Failed to save role");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Role update(Role role) throws ElderSphereException {
        try {
            return roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate role name on update: {}", e.getMessage());
            throw BadRequestException.conflict("Role already exists with name: " + role.getName());
        } catch (Exception e) {
            log.error("Failed to update role id {}: {}", role.getId(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to update role");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws ElderSphereException {
        try {
            roleRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete role id {}: {}", id, e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.PROCESSING_ERROR, "Failed to delete role");
        }
    }

    public Role findById(Long id) {
        return roleRepository.findByIdWithPermissions(id).orElse(null);
    }

    public Role findByIdOrThrow(Long id) {
        return roleRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Role", id));
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public List<Role> findAll() {
        return roleRepository.findAllWithPermissions();
    }
}
