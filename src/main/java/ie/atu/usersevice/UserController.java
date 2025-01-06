package ie.atu.usersevice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserDetails userDetails) {
        try {
            UserDetails newUser = userService.register(userDetails);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDetails userDetails) {
        System.out.println("Username: " + userDetails.getUsername());
        System.out.println("Password: " + userDetails.getPassword());

        // Fetch user from the database by username
        Optional<UserDetails> userFromDb = userRepository.findByUsername(userDetails.getUsername());

        if (userFromDb.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails existingUser = userFromDb.get();

        // Check if the password matches
        if (!existingUser.getPassword().equals(userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Generate a new token
        String token = JwtUtil.generateToken(existingUser.getUsername());
        existingUser.setToken(token);
        existingUser.setLastLogin(LocalDateTime.now());

        userRepository.save(existingUser);

        return ResponseEntity.ok(Map.of("token", token));
    }


    @GetMapping("/user/{username}")
    public Optional<UserDetails> getUserProfile(@PathVariable String username) {
        return userService.getUserProfile(username);
    }

}
