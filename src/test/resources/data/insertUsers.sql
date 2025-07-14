insert into users(username, password, email, subscribers)
values ('Sweezy', '$2a$04$iUUP1k.OvgE4FJ8VEEH3be1TzwduMoql0COzztviRs00Cugiq6oZG', 'sweezy@gmail.com', 0),
       ('Lira', '$2a$04$iUUP1k.OvgE4FJ8VEEH3be1TzwduMoql0COzztviRs00Cugiq6oZG', 'lira@gmail.com', 0),
       ('user', '$2a$04$iUUP1k.OvgE4FJ8VEEH3be1TzwduMoql0COzztviRs00Cugiq6oZG', 'user@gmail.com', 0);

insert into m2m_users_roles(role_id, user_id)
values (1, 1),
       (1, 2),
       (1, 3);