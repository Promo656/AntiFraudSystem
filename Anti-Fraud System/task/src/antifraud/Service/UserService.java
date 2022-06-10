package antifraud.Service;

import antifraud.DeleteEntity;
import antifraud.Enums.Roles;
import antifraud.Models.ResponseRole;
import antifraud.Models.User;
import antifraud.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Component
public class UserService {
    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<User> register(User user) {
        User checkedUser = repository.findUserByUsername(user.getUsername().toLowerCase());
        if (checkedUser == null) {
            Roles role = repository.findAll().size() > 0 ? Roles.MERCHANT : Roles.ADMINISTRATOR;
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUsername(user.getUsername().toLowerCase());
            user.setRole(role.toString());
            repository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    public ResponseEntity<List<User>> getUsers() {
        List<User> users = repository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    public ResponseEntity<DeleteEntity> deleteUser(String username) {
        User user = repository.findUserByUsername(username);
        if (user != null) {
            repository.delete(user);
            return new ResponseEntity<>(new DeleteEntity(username), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<User> changeUserRole(ResponseRole newUserRole) {
        User user = repository.findUserByUsername(newUserRole.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!Objects.equals(user.getRole(), Roles.ADMINISTRATOR.toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (user.getRole().equals(newUserRole.getRole())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
