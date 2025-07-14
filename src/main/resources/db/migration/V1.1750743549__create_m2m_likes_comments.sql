create table if not exists m2m_likes_comments(
    comment_id bigint references comments(id),
    user_id bigint references users(id) unique
);