create table if not exists roles(
    id bigserial primary key,
    role_name varchar not null unique
);

insert into roles(role_name)
values ('ACTIVE'),
       ('BANNED'),
       ('MUTED');

create table if not exists m2m_users_roles(
    role_id bigint default 1,
    foreign key (role_id) references roles(id),
    user_id bigint references users(id)
);