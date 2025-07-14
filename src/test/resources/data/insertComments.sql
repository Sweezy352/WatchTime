insert into comments(content, likes, dislikes, date_created, user_id, amount_comments, views)
values ('test comment', 0, 0, now(), 1, 0, 0),
       ('test comment1', 0, 0, now(), 2, 0, 0),
       ('test comment2', 0, 0, now(), 3, 0, 0);

insert into m2m_comments_videos(video_id, comment_id)
values (3, 1),
       (2, 3),
       (1, 2);