create table if not exists m2m_comments(
    comment_id_original bigint references comments(id),
    comment_id_reply bigint references comments(id) unique
);

alter table comments drop column video_id;

create table if not exists m2m_comments_videos(
    video_id bigint references videos(id),
    comment_id bigint references comments(id) unique
);