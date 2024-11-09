package ie.atu.usersevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDetails registerUser(@RequestBody UserDetails userDetails) {
        return userService.register(userDetails);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody String username, String password) {
        return userService.login(username, password);
    }
    @GetMapping("/user/{username}")
    public UserDetails getUserProfile(@PathVariable String username) {
        return userService.getUserProfile(username);
    }
}
