package ie.atu.usersevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails register(UserDetails user) {

        return userRepository.save(user);
    }

    public Optional<UserDetails> login(String username, String password) {
        Optional<UserDetails> user = userRepository.findByUsername(username);
        return user.filter(u -> u.getPassword().equals(password));
    }

    public Optional<UserDetails> getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }

}
