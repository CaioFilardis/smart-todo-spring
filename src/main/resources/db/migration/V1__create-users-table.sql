-- Padrão snake_case, nomes em inglês e correção do 'password'.
CREATE TABLE users (
                       id           BIGINT       NOT NULL AUTO_INCREMENT,
                       full_name    VARCHAR(255) NOT NULL,
                       email        VARCHAR(255) NOT NULL,
                       password     VARCHAR(255) NOT NULL,
                       created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_email (email)
);