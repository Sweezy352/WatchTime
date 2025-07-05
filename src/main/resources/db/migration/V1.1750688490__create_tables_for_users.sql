create table if not exists users(
    id bigserial primary key,
    username varchar not null unique,
    password varchar not null,
    email varchar not null unique,
    uuid varchar default null,
    subscribers bigint default 0,
    is_premium boolean default false,
    date_created DATE default now()
);

create table if not exists m2m_subscribers_users(
    user_id bigint references users(id),
    user_id_subscriber bigint references users(id) unique
);

create table if not exists profile_picture(
    id bigserial primary key,
    file_name varchar not null unique,
    user_id bigint references users(id) on delete cascade unique
);

create table if not exists videos(
    id bigserial primary key,
    title varchar not null,
    description varchar not null,
    views bigint default 0,
    likes bigint default 0,
    dislikes bigint default 0,
    amount_comments bigint default 0,
    date_created DATE default now(),
    file_name varchar not null unique,
    user_id bigint references users(id) on delete cascade unique
);

create table if not exists m2m_likes_videos(
    user_id bigint references users(id),
    video_id bigint references videos(id) unique
);

create table if not exists m2m_play_list_videos(
    user_id bigint references users(id),
    video_id bigint references videos(id) unique
);

create table if not exists m2m_history_videos(
    user_id bigint references users(id),
    video_id bigint references videos(id) unique
);

create table if not exists preview_video(
    id bigserial primary key,
    file_name varchar not null unique,
    video_id bigint references videos(id) on delete cascade unique
);

create table if not exists comments(
    id bigserial primary key,
    content varchar not null,
    likes bigint default 0,
    dislikes bigint default 0,
    date_created DATE default now(),
    video_id bigint references videos(id),
    user_id bigint references users(id)
);

create table if not exists reply_comments(
    id bigserial primary key,
    content varchar not null,
    likes bigint default 0,
    dislikes bigint default 0,
    date_created DATE default now(),
    comment_id bigint references comments(id),
    user_id bigint references users(id)
);