package com.eldersphere.core.service;

import com.eldersphere.core.dao.ConfigurationDao;
import com.eldersphere.core.dto.configuration.ConfigurationListResponseDTO;
import com.eldersphere.core.dto.configuration.ConfigurationRequestBodyDTO;
import com.eldersphere.core.dto.configuration.ConfigurationResponseDTO;
import com.eldersphere.core.entities.Configuration;
import com.eldersphere.core.utils.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationDao configurationDao;
    private final ModelMapper modelMapper;
    private final JsonHelper jsonHelper;

    public List<ConfigurationResponseDTO> getConfigurationByContext(String contextsAsString) {
        List<String> contexts = (contextsAsString != null && !contextsAsString.isEmpty())
                ? Arrays.asList(contextsAsString.split(","))
                : null;
        return configurationDao.getConfigurationByContext(contexts);
    }

    @Transactional(rollbackFor = Exception.class)
    public Configuration saveConfiguration(ConfigurationRequestBodyDTO dto) {
        dto.setData(jsonHelper.filterDuplicates(dto.getData()));
        Configuration configuration = modelMapper.map(dto, Configuration.class);
        return configurationDao.upsertConfiguration(configuration);
    }

    @Transactional(rollbackFor = Exception.class)
    public Configuration updateConfiguration(Long configurationId, ConfigurationRequestBodyDTO dto) {
        dto.setData(jsonHelper.filterDuplicates(dto.getData()));
        Configuration existing = configurationDao.getConfigurationById(configurationId);
        if (existing != null) {
            existing.setContext(dto.getContext());
            existing.setData(dto.getData().toString());
            return configurationDao.upsertConfiguration(existing);
        }
        return null;
    }

    public Page<ConfigurationListResponseDTO> getAllConfigurationsByCriteria(String search, Pageable pageable) {
        return configurationDao.findAllConfigurationsByCriteria(search, pageable);
    }
}
