create table tasks
(
    id             bigint       not null auto_increment,
    user_id        bigint       not null,
    titulo         varchar(255) not null,
    descricao      text null,
    status         enum('PENDENTE', 'COMPLETO') not null DEFAULT 'PENDENTE',
    prioridade     enum('ALTO', 'MEDIO', 'BAIXO') null,
    dataVencimento date null,
    criadoEm       timestamp    not null default CURRENT_TIMESTAMP,
    atualizadoEm   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    primary key (id),

    constraint fk_tasks_users
        foreign key (user_id)
            references users (id)
            On delete cascade
);