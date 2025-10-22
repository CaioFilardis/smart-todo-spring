-- Padrão snake_case, nomes em inglês e correção dos ENUMs.
CREATE TABLE tasks (
                       id           BIGINT                             NOT NULL AUTO_INCREMENT,
                       user_id      BIGINT                             NOT NULL,
                       title        VARCHAR(255)                       NOT NULL,
                       description  TEXT                               NULL,
                       status       ENUM('PENDING', 'COMPLETED')       NOT NULL DEFAULT 'PENDING',
                       priority     ENUM('HIGH', 'MEDIUM', 'LOW')      NULL,
                       due_date     DATE                               NULL,
                       created_at   TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at   TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),

                       CONSTRAINT fk_tasks_users
                           FOREIGN KEY (user_id)
                               REFERENCES users (id)
                               ON DELETE CASCADE
);