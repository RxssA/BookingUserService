package ie.atu.usersevice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userDetails = new UserDetails();
        userDetails.setId(1);
        userDetails.setUsername("testuser");
        userDetails.setPassword("password123");
        userDetails.setFirstName("John");
        userDetails.setLastName("Pork");
        userDetails.setEmail("john.Pork@example.com");
        userDetails.setPhone("1234567890");
    }

    @Test
    void registerUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.register(Mockito.any(UserDetails.class))).thenReturn(userDetails);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("john.Pork@example.com"));

        verify(userService, times(1)).register(Mockito.any(UserDetails.class));
    }

    @Test
    void registerUser_ShouldReturnInternalServerErrorOnFailure() throws Exception {
        when(userService.register(Mockito.any(UserDetails.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).register(Mockito.any(UserDetails.class));
    }

    @Test
    void loginUser_ShouldReturnSuccess() throws Exception {
        when(userService.login("testuser", "password123")).thenReturn(Optional.of(userDetails));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("UserDetails(id=1, username=testuser, ...)"));

        verify(userService, times(1)).login("testuser", "password123");
    }

    @Test
    void loginUser_ShouldReturnInternalServerErrorOnFailure() throws Exception {
        when(userService.login("testuser", "wrongpassword")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).login("testuser", "wrongpassword");
    }

    @Test
    void getUserProfile_ShouldReturnUserProfile() throws Exception {
        when(userService.getUserProfile("testuser")).thenReturn(Optional.of(userDetails));

        mockMvc.perform(get("/api/users/user/testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("john.Pork@example.com"));

        verify(userService, times(1)).getUserProfile("testuser");
    }

    @Test
    void getUserProfile_ShouldReturnEmptyWhenNotFound() throws Exception {
        when(userService.getUserProfile("nonexistentuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/user/nonexistentuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService, times(1)).getUserProfile("nonexistentuser");
    }
}