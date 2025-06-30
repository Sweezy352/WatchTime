package kg.sweezy.watchtime.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl implements PasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Autowired
    public PasswordEncoderImpl(Environment env) {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        this.env = env;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if(env.matchesProfiles("develop")){
            return bCryptPasswordEncoder.encode(rawPassword);
        }
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
