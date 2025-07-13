alter table comments add column if not exists amount_comments bigint default 0;
alter table comments add column if not exists views bigint default 0;