create table if not exists m2m_comments_dislikes(
    comment_id bigint references comments(id),
    user_id bigint references users(id) unique
);