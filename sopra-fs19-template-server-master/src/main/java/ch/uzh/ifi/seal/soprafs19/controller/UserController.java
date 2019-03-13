package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import ch.uzh.ifi.seal.soprafs19.exceptions.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.Serializable;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    //FOR DEBUGGING PURPOSES
    @GetMapping("/debug/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @GetMapping("/users")
    Iterable<User> all(@RequestParam() String token) {
        if (service.validateToken(token)) {
            return service.getUsers();
        }
        else {
            throw new AuthenticationException("token invalid");
        }
    }

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id, @RequestParam() String token) {
        System.out.print(service.validateToken(token));
        if (service.validateToken(token)){
            User other = service.getUserById(id);
            if (other == null) {
                throw new UserNotFoundException("user with userId: "+ id + " was not found");
            }
            return other;
            //return service.getUserById(id);
        }
        else {
            throw new AuthenticationException("token invalid");
        }}

    @CrossOrigin
    @PutMapping("/users/{id}")
    ResponseEntity updateUser(@RequestBody User newUser, @PathVariable long id, @RequestParam String token) {
        User user = service.getUserById(id);
        User tokenCheck = service.getUserByToken(token);
        if (user == null){
            throw new UserNotFoundException("user with userId: "+ id + " was not found");
        } else if (!user.getToken().equals(tokenCheck.getToken())) {
            throw new AuthenticationException("Invalid token " + token);
        }
        this.service.updateUser(user, newUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/users/login")
    AuthorizationCredentials login(@RequestBody LoginCredentials cred) {
        AuthorizationCredentials acred = new AuthorizationCredentials();
        User local = this.service.loginUser(cred.username, cred.password);
        acred.token = local.getToken();
        acred.id = local.getId();
        return acred;
    }

    @PostMapping("/users/logout")
    @ResponseStatus(HttpStatus.OK)
    String logout(@RequestBody AuthorizationCredentials cred) {
        return this.service.logoutUser(cred.token);
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }
}

class AuthorizationCredentials implements Serializable {
    public String token;
    public Long id;
}


class LoginCredentials implements Serializable {
    public String username;
    public String password;
}