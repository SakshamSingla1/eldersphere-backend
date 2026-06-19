package com.eldersphere.core.exceptions;

import com.eldersphere.core.enums.ExceptionCodeEnum;

public class ResourceNotFoundException extends ElderSphereException {

    public ResourceNotFoundException(String message) {
        super(ExceptionCodeEnum.RESOURCE_NOT_FOUND, message);
    }

    public static ResourceNotFoundException of(String resource, Long id) {
        return new ResourceNotFoundException(resource + " not found with id: " + id);
    }

    public static ResourceNotFoundException of(String message) {
        return new ResourceNotFoundException(message);
    }
}
