package antifraud.Service;

import antifraud.Models.DeleteEntity;
import antifraud.Enums.Access;
import antifraud.Enums.Roles;
import antifraud.Models.RequestAccess;
import antifraud.Models.RequestRole;
import antifraud.Models.ResponseOperationStatus;
import antifraud.Models.User;
import antifraud.Repositories.UserRepository;
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
            boolean isAdmin = !(repository.findAll().size() > 0);
            Roles role = isAdmin ? Roles.ADMINISTRATOR : Roles.MERCHANT;
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUsername(user.getUsername().toLowerCase());
            user.setRole(role.toString());
            user.setAccountNonLocked(isAdmin);
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

    public ResponseEntity<User> changeUserRole(RequestRole newUserRole) {
        User user = repository.findUserByUsername(newUserRole.getUsername());
        String newRole = newUserRole.getRole();
        List<String> roles = List.of(Roles.MERCHANT.toString(), Roles.SUPPORT.toString());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!roles.contains(newRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (user.getRole().equals(newUserRole.getRole())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        user.setRole(newRole);
        repository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<ResponseOperationStatus> changeUserAccess(RequestAccess newUserAccess) {
        User user = repository.findUserByUsername(newUserAccess.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        boolean isNonLocked = !Objects.equals(newUserAccess.getOperation(), Access.LOCK.toString());
        user.setAccountNonLocked(isNonLocked);
        repository.save(user);
        String status = Objects.equals(newUserAccess.getOperation(), Access.LOCK.toString()) ? "locked" : "unlocked";
        String msg = String.format("User %s %s!", user.getUsername(), status);
        return new ResponseEntity<>(new ResponseOperationStatus(msg), HttpStatus.OK);
    }
}
