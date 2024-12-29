package ie.atu.usersevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void registerUser_ShouldReturnCreatedUser() throws Exception {
        UserDetails userDetails = new UserDetails();
        when(userService.register(any(UserDetails.class))).thenReturn(userDetails);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"johnPork\",\"password\":\"password123\",\"name\":\"John Pork\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("JohnPork"))
                .andExpect(jsonPath("$.name").value("John Pork"));

        verify(userService, times(1)).register(any(UserDetails.class));
    }

    @Test
    void registerUser_ShouldReturnInternalServerErrorOnException() throws Exception {
        when(userService.register(any(UserDetails.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"JohnPork\",\"password\":\"password123\",\"name\":\"John Pork\"}"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).register(any(UserDetails.class));
    }

    /*@Test
    void loginUser_ShouldReturnOkWhenUserExists() throws Exception {
        when(userService.login("JohnPork", "password123")).thenReturn(Optional.of("token123"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"JohnPork\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("token123"));

        verify(userService, times(1)).login("john_doe", "password123");
    }*/

    @Test
    void loginUser_ShouldReturnInternalServerErrorWhenUserDoesNotExist() throws Exception {
        when(userService.login("JohnPork", "password123")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"JohnPork\",\"password\":\"password123\"}"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).login("JohnPork", "password123");
    }

    @Test
    void getUserProfile_ShouldReturnUserProfileWhenExists() throws Exception {
        UserDetails userDetails = new UserDetails();
        when(userService.getUserProfile("JohnPork")).thenReturn(Optional.of(userDetails));

        mockMvc.perform(get("/api/users/user/JohnPork"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("JohnPork"))
                .andExpect(jsonPath("$.name").value("John Pork"));

        verify(userService, times(1)).getUserProfile("JohnPork");
    }

    @Test
    void getUserProfile_ShouldReturnEmptyWhenUserDoesNotExist() throws Exception {
        when(userService.getUserProfile("JohnPork")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/user/JohnPork"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService, times(1)).getUserProfile("JohnPork");
    }
}