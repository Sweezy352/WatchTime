create table if not exists m2m_dislikes_videos(
    user_id bigint references users(id),
    video_id bigint references videos(id) unique
);

alter table m2m_likes_videos drop constraint m2m_likes_videos_user_id_key;
