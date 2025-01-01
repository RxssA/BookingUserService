package ie.atu.usersevice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserRepository userRepository;

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

        // Fetch user from database
        Optional<UserDetails> userFromDb = userRepository.findByUsername(userDetails.getUsername());

        // Check if user exists and the password matches
        if (userFromDb.isEmpty() || !userDetails.getPassword().equals(userFromDb.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Generate token
        String token = JwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }


    @GetMapping("/user/{username}")
    public Optional<UserDetails> getUserProfile(@PathVariable String username) {
        return userService.getUserProfile(username);
    }

}
