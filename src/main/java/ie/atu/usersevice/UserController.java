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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        // Validate user credentials (mock example here)
        if (!"correctUsername".equals(userDetails.getUsername()) || !"correctPassword".equals(userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Generate token if valid
        String token = JwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }


    @GetMapping("/user/{username}")
    public Optional<UserDetails> getUserProfile(@PathVariable String username) {
        return userService.getUserProfile(username);
    }

}
