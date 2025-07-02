create table if not exists m2m_subscription_users(
    subscriber_id bigint references users(id),
    channel_id bigint references users(id)
);