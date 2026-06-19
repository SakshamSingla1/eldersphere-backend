CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255),
    phone         VARCHAR(20),
    password_hash VARCHAR(255),
    role          VARCHAR(50)  NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    region_id     BIGINT,
    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by    BIGINT,
    updated_by    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email  UNIQUE (email),
    CONSTRAINT uq_users_phone  UNIQUE (phone),
    CONSTRAINT fk_users_region FOREIGN KEY (region_id) REFERENCES regions (id)
);

-- Self-referencing audit FKs (must come after table exists)
ALTER TABLE users
    ADD CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL;

-- Back-fill audit FKs for tables created before users
ALTER TABLE regions
    ADD CONSTRAINT fk_regions_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_regions_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL;

ALTER TABLE roles
    ADD CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL;

ALTER TABLE permissions
    ADD CONSTRAINT fk_permissions_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_permissions_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL;
