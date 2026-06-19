package com.eldersphere.core.dao;

import com.eldersphere.core.dto.configuration.ConfigurationListResponseDTO;
import com.eldersphere.core.dto.configuration.ConfigurationResponseDTO;
import com.eldersphere.core.entities.Configuration;
import com.eldersphere.core.exceptions.ConflictException;
import com.eldersphere.core.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ConfigurationDao {

    private final ConfigurationRepository configurationRepository;

    public List<ConfigurationResponseDTO> getConfigurationByContext(List<String> contexts) {
        return configurationRepository.findConfigurationByContext(contexts);
    }

    public Configuration getConfigurationById(Long id) {
        return configurationRepository.findById(id).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public Configuration upsertConfiguration(Configuration configuration) {
        try {
            Configuration saved = configurationRepository.save(configuration);
            configurationRepository.flush();
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Duplicate entry for Configuration");
        } catch (Exception e) {
            throw new RuntimeException("Failed to save Configuration", e);
        }
    }

    public Page<ConfigurationListResponseDTO> findAllConfigurationsByCriteria(String search, Pageable pageable) {
        return configurationRepository.findAllConfigurationsByCriteria(search, pageable);
    }
}
