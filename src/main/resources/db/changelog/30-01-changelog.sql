-- liquibase formatted sql

-- changeset e_cha:1726476397331-1
CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726476397331-2
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726476397331-3
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726476397331-4
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1726476397331-5
CREATE TABLE client
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

-- changeset e_cha:1726476397331-6
CREATE TABLE account
(
    id BIGINT PRIMARY KEY,
    client_id BIGINT,
    account_type varchar,
    balance DECIMAL(19, 2),
    is_blocked BOOLEAN NOT NULL
);

-- changeset e_cha:1726476397331-7
CREATE TABLE transaction
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id),
    amount DECIMAL(19, 2),
    client_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    is_cancelled BOOLEAN NOT NULL
);

-- changeset e_cha:1726476397331-8
CREATE TABLE role
(
    id   BIGINT NOT NULL,
    name VARCHAR(20),
    CONSTRAINT pk_role PRIMARY KEY (id)
);

-- changeset e_cha:1726476397331-9
CREATE TABLE users
(
    id       BIGINT NOT NULL,
    login    VARCHAR(20),
    email    VARCHAR(50),
    password VARCHAR(120),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset e_cha:1726476397331-10
CREATE TABLE user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);