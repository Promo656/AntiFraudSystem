package antifraud.Controller;

import antifraud.DeleteEntity;
import antifraud.Service.UserService;
import antifraud.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<DeleteEntity> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

}
