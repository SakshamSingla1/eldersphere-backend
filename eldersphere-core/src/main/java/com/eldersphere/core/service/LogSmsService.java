package com.eldersphere.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnMissingBean(name = "realSmsService")
public class LogSmsService implements SmsService {
    @Override
    public void send(String phone, String message) {
        log.info("[SMS MOCK] To: {} | Message: {}", phone, message);
    }
}
