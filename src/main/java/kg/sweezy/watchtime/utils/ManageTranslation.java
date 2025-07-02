package kg.sweezy.watchtime.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ManageTranslation {
    private final MessageSource messageSource;

    @Autowired
    public ManageTranslation(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getTranslation(String translationKey) {
        return messageSource.getMessage(translationKey, null, LocaleContextHolder.getLocale());
    }
}
