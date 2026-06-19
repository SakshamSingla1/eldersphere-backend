CREATE TABLE user_profiles (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    user_id             BIGINT       NOT NULL,
    full_name           VARCHAR(255) NOT NULL,
    date_of_birth       DATE,
    gender              VARCHAR(20),
    profile_photo_url   TEXT,
    language_preference VARCHAR(20)  NOT NULL DEFAULT 'en',
    created_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by          BIGINT,
    updated_by          BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id),
    CONSTRAINT fk_user_profiles_user    FOREIGN KEY (user_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_profiles_created FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_user_profiles_updated FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);
