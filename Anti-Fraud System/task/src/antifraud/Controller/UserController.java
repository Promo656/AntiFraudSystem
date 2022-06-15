package antifraud.Controller;

import antifraud.DeleteEntity;
import antifraud.Models.RequestAccess;
import antifraud.Models.RequestRole;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Service.UserService;
import antifraud.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/role")
    public ResponseEntity<User> changeUserRole(@RequestBody RequestRole newUserRole) {
        return userService.changeUserRole(newUserRole);
    }

    @PutMapping("/access")
    public ResponseEntity<ResponseOperationStatus> details(@RequestBody RequestAccess newUserAccess) {
        return userService.changeUserAccess(newUserAccess);
    }

}
