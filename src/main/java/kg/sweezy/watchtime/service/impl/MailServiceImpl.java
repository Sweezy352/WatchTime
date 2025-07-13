package kg.sweezy.watchtime.service.impl;

import jakarta.mail.internet.MimeMessage;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.MailSendException;
import kg.sweezy.watchtime.service.MailService;
import kg.sweezy.watchtime.service.ProfilePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {
    @Value("${spring.mail.username}")
    private String mainEmail;
    private final JavaMailSender mailSender;
    private final ProfilePictureService profilePictureService;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, ProfilePictureService profilePictureService) {
        this.mailSender = mailSender;
        this.profilePictureService = profilePictureService;
    }

    @Override
    public void sendMessageTo(String email, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mainEmail);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        }catch (Exception e) {
            throw new MailSendException("error.messageSend");
        }
    }

    @Override
    public void sendMessageToWithAttachment(String email, String subject, String text) {
    }

    @Override
    public void notifyAllSubscribers(UserEntity userEntity, VideoEntity videoEntity ,String subject) throws MailSendException {
        if(userEntity.getSubscribersList() == null || userEntity.getSubscribersList().isEmpty()) return;

        userEntity.getSubscribersList().parallelStream().forEach(subscriber -> {
            sendMessageWithHtmlFromChannel(userEntity, videoEntity, subscriber.getEmail(), subject);
        });
    }

    @Override
    public void verificationEmail(String email,String subject, String uuid) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(mainEmail);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            try(var inputStream = Objects.requireNonNull(MailServiceImpl.class.getClassLoader().getResourceAsStream("templates/verification-email.html"))) {
                String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                String htmlContent = String.format(html, uuid);
                mimeMessageHelper.setText(htmlContent, true);
                mailSender.send(mimeMessage);
            }
        }catch (Exception e) {
            throw new MailSendException("error.messageSend");
        }
    }

    @Override
    public void sendMessageWithHtmlFromChannel(UserEntity userEntity, VideoEntity videoEntity, String email, String subject){
        if(userEntity == null) return;
        if(videoEntity == null) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(mainEmail);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            try(var inputStream = Objects.requireNonNull(MailServiceImpl.class.getClassLoader().getResourceAsStream("/templates/mailindex.html"))) {
                String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                String videoLink = "http://localhost:8080/api/video/get-by-id/" + videoEntity.getId();

                String htmlContent = String.format(html, videoEntity.getTitle(), "From: " + userEntity.getUsername() + "WatchTime Channel", videoLink);
                if (userEntity.getProfilePicture() == null) messageHelper.addInline("logo", new File(""));
                if (userEntity.getProfilePicture() != null) messageHelper.addInline("logo", new InputStreamResource(profilePictureService.getProfilePictureByFileName(userEntity.getProfilePicture().getFileName())));

                messageHelper.setText(htmlContent, true);
                mailSender.send(message);
            }

        }catch (Exception e){
            throw new MailSendException("error.messageSend");
        }
    }
}
