CREATE TABLE otp_verifications (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    identifier  VARCHAR(255) NOT NULL,
    otp_hash    VARCHAR(255) NOT NULL,
    purpose     VARCHAR(50)  NOT NULL,
    expires_at  TIMESTAMP(6) NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by  BIGINT,
    updated_by  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_otp_identifier_purpose UNIQUE (identifier, purpose),
    CONSTRAINT fk_otp_created FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_otp_updated FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);
