CREATE TABLE user_social_accounts (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT       NOT NULL,
    provider         VARCHAR(50)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    access_token_enc TEXT,
    linked_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at       TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by       BIGINT,
    updated_by       BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_social_provider_user UNIQUE (provider, provider_user_id),
    CONSTRAINT fk_social_user          FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_social_created       FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_social_updated       FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);
