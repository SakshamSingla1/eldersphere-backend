package com.eldersphere.adminapi.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotpSetupResponse {
    private String secret;        // raw secret for manual entry
    private String qrUri;         // otpauth:// URI for QR code
    private String qrCodeBase64;  // data:image/png;base64,... — render directly as <img>
    private String method;
}
