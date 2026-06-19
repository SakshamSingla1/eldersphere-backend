CREATE TABLE notification_template_variables (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    variable_name    VARCHAR(255),
    html_content     LONGTEXT,
    whatsapp_variable BIGINT,
    template_id      BIGINT,
    created_at       TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_ntv_template FOREIGN KEY (template_id) REFERENCES notification_templates(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
