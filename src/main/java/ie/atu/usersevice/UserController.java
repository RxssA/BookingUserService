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
    private AdminServiceClient adminServiceClient;

    public UserController(UserRepository userRepository, UserService userService, AdminServiceClient adminServiceClient) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.adminServiceClient = adminServiceClient;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserDetails userDetails) {
        userDetails.setToken(null);
        userDetails.setLastLogin(null);
        try {
            UserDetails newUser = userService.register(userDetails);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDetails userDetails) {
        System.out.println("Username: " + userDetails.getUsername());
        System.out.println("Password: " + userDetails.getPassword());

        // Fetch user from the database by username
        Optional<UserDetails> userFromDb = userRepository.findByUsername(userDetails.getUsername());
        userDetails.setToken(null);
        userDetails.setLastLogin(null);
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

    @GetMapping("api/admins/{id}")
    public ResponseEntity<adminUser> getAdminDetails(@PathVariable String id) {
        adminUser admin = adminServiceClient.getAllUsers();
        return ResponseEntity.ok(admin);
    }
}
