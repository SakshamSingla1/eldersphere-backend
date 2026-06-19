CREATE TABLE user_fcm_tokens (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(500) NOT NULL,
    device_type VARCHAR(20),
    app_version VARCHAR(20),
    last_active TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by  BIGINT,
    updated_by  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_fcm_user_token UNIQUE (user_id, token),
    CONSTRAINT fk_fcm_user       FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_fcm_created    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_fcm_updated    FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);
