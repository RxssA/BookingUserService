package ie.atu.usersevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails register(UserDetails user) {
        //add set password stuff
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        //check if password is correct
        UserDetails user = userRepository.findByUsername(username);
        return username;
    }

    public UserDetails getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }

}
