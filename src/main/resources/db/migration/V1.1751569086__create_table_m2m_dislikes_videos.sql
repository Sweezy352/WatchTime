create table if not exists m2m_dislikes_videos(
    user_id bigint references users(id),
    video_id bigint references videos(id) unique
);
