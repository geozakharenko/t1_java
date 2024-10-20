-- changeset e_cha:1726477659739-1
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);
ALTER TABLE users
    ADD CONSTRAINT uc_f8d2576e807e2b20b506bf6a3 UNIQUE (login);

-- changeset e_cha:1726477659739-2
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES role (id);
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset e_cha:1726477659739-3
ALTER TABLE client
    ADD first_name VARCHAR(255);
ALTER TABLE client
    ADD last_name VARCHAR(255);
ALTER TABLE client
    ADD middle_name VARCHAR(255);

-- changeset e_cha:1726477659739-4
INSERT INTO role VALUES (1, 'ROLE_USER');

-- changeset e_cha:1726477659739-5
INSERT INTO role VALUES (2, 'ROLE_MODERATOR');

-- changeset e_cha:1726477659739-6
INSERT INTO role VALUES (3, 'ROLE_ADMIN');