package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;

import java.util.List;

public interface MailService {
    public void sendMessageTo(String email, String subject, String text);
    public void sendMessageToWithAttachment(String email, String subject, String text);
    public void notifyAllSubscribers(UserEntity userEntity, VideoEntity videoEntity ,String subject);
    public void verificationEmail(String email,String subject,String uuid);
    public void sendMessageWithHtmlFromChannel(UserEntity userEntity, VideoEntity videoEntity, String email, String subject);
}
