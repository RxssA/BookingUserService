package ie.atu.usersevice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<UserDetails, Long> {
    UserDetails findByUsername(String Username);

}
