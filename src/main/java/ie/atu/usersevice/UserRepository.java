package ie.atu.usersevice;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableFeignClients
public interface UserRepository extends MongoRepository<UserDetails, String> {
    Optional<UserDetails> findByUsername(String Username);
}
