package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserAlreadyExistsException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
import ch.uzh.ifi.seal.soprafs19.exceptions.PasswordNotValidException;

import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public String loginUser(String username, String password) {
        User temp = this.userRepository.findByUsername(username);
        if (temp == null) throw new UserNotFoundException(username);
        if (temp.getPassword().equals(password)) {
            temp.setStatus(UserStatus.ONLINE);
            temp.setToken(UUID.randomUUID().toString());
            log.debug("User {} logged in!", username);
            return temp.getToken();
        }
        else throw new PasswordNotValidException(username);
    }

    public String logoutUser(String token) {
        User temp = this.userRepository.findByToken(token);
        if (temp == null) {
            throw new AuthenticationException("Token invalid");
        }
        temp.setStatus(UserStatus.OFFLINE);
        temp.setToken(null);
        return "logout successful!";
    }

    public User createUser(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()) != null){ throw new UserAlreadyExistsException(newUser.getUsername()); }
        newUser.setStatus(UserStatus.OFFLINE);
        Calendar today = Calendar.getInstance();
        newUser.setCreationDate(today.getTime());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User getUser(String username) {
        User temp = this.userRepository.findByUsername(username);
        if (temp == null) throw new UserNotFoundException("User not found!");
        return temp;
    }

    public Boolean validateToken(String token) {
        return this.userRepository.findByToken(token) != null;
    }
}