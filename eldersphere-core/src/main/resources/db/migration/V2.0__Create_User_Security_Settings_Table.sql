CREATE TABLE user_security_settings (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    user_id               BIGINT       NOT NULL,
    totp_secret_encrypted TEXT,
    totp_enabled          BOOLEAN      NOT NULL DEFAULT FALSE,
    two_factor_method     VARCHAR(20),
    last_password_change  TIMESTAMP(6),
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    locked_until          TIMESTAMP(6),
    created_at            TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at            TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by            BIGINT,
    updated_by            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_user_security_user_id UNIQUE (user_id),
    CONSTRAINT fk_user_security_user    FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_security_created FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_user_security_updated FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);
