package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.UserEntity;

public interface NotificationService {
    public void sendNotificationVideoWithEmail(UserEntity userEntity);
}
