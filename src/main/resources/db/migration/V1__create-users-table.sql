create table users
(
    id          bigint       not null auto_increment,
    nome         varchar(255) not null,
    email        varchar(255) not null,
    passoword    varchar(255) not null,
    criadoEm     timestamp    not null default current_timestamp,
    atualizadoEm timestamp    not null default current_timestamp on update current_timestamp,

    primary key (id),
    unique key uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;