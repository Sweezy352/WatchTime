create table if not exists roles(
    id bigserial primary key,
    role_name varchar not null unique
);

insert into roles(role_name)
values ('ACTIVE'),
       ('BANNED'),
       ('MUTED');

create table if not exists m2m_users_roles(
    role_id bigint references roles(id),
    user_id bigint references users(id)
);