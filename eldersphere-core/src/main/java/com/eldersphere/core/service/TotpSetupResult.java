package com.eldersphere.core.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotpSetupResult {
    private final String secret;
    private final String qrUri;
    private final String qrCodeBase64;  // data:image/png;base64,<value>
}
