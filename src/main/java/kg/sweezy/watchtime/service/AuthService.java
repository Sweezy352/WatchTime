package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    public String login(String username, String password);
    public UserEntity getCurrentUser();
}
