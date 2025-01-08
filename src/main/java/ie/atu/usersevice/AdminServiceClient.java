package ie.atu.usersevice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081/api/users")
public interface AdminServiceClient {
    @GetMapping("/{id}")
    ResponseEntity<UserDetails> getUserById(@PathVariable String id);

    @GetMapping
    adminUser getAllUsers();
}
