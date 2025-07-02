package kg.sweezy.watchtime.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@Table(name = "roles")
@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RoleEntity extends BaseEntity implements GrantedAuthority {
    @Column(name = "role_name")
    private String roleName;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "m2m_users_roles",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<UserEntity> users;

    @PrePersist
    public void prePersist(){
        if(roleName.isEmpty())roleName = "ACTIVE";
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
