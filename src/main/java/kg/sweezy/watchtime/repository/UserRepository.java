package kg.sweezy.watchtime.repository;

import kg.sweezy.watchtime.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    @Query("select u from UserEntity u where u.username like concat(:username, '%')")
    List<UserEntity> findByUsernameStartingWith(String username);
    List<UserEntity> findByIdGreaterThanOrderByIdAsc(Long afterId, Pageable pageable);
}
