package ie.atu.usersevice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private AdminServiceClient adminServiceClient;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegisterUser() throws Exception {
        UserDetails user = new UserDetails();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("123456789");

        // Mock the service to return the created user
        when(userService.register(any(UserDetails.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUsername"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("123456789"));
    }

    @Test
    public void testLoginUserValid() throws Exception {
        UserDetails user = new UserDetails();
        user.setUsername("testUsername");
        user.setPassword("testPassword");

        String token = "generated-token";
        when(userRepository.findByUsername("testUsername")).thenReturn(Optional.of(user));
        when(userService.login("testUsername", "testPassword")).thenReturn(Optional.of(user));
        when(JwtUtil.generateToken("testUsername")).thenReturn(token);

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    public void testLoginUserInvalid() throws Exception {
        UserDetails user = new UserDetails();
        user.setUsername("testUsername");
        user.setPassword("wrongPassword");

        // Mock repository return value
        when(userRepository.findByUsername("testUsername")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())  // Expect 401 Unauthorized
                .andExpect(content().string("Invalid credentials"));  // Expect the error message
    }


    @Test
    public void testRegisterUserError() throws Exception {
        UserDetails user = new UserDetails();
        user.setUsername("testUsername");
        user.setPassword("testPassword");

        // Mock the service to throw an exception
        when(userService.register(any(UserDetails.class))).thenThrow(new RuntimeException("Registration error"));

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isInternalServerError())  // Expecting 500 error
                .andExpect(content().string("Error: Registration error"));
    }
}
