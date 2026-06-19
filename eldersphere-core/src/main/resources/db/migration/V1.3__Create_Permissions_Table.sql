CREATE TABLE permissions (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    resource    VARCHAR(100) NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    description TEXT,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by  BIGINT,
    updated_by  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uq_permissions_resource_action UNIQUE (resource, action)
);
