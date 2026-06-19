CREATE TABLE audit_logs (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    actor_id      BIGINT,
    action        VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id   BIGINT,
    ip_address    VARCHAR(45),
    user_agent    TEXT,
    metadata      JSON,
    created_at    TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_audit_actor   FOREIGN KEY (actor_id)   REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_audit_created FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_logs_actor     ON audit_logs (actor_id);
CREATE INDEX idx_audit_logs_resource  ON audit_logs (resource_type, resource_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs (created_at DESC);
