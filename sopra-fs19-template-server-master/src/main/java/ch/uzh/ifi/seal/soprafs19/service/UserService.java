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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username){
        return this.userRepository.findByUsername(username);
    }

    public User getUserByToken(String token){
        return this.userRepository.findByToken(token);
    }

    public User getUserById(Long id){
        Optional<User> user = this.userRepository.findById(id);
       // if (user.isPresent()) {
            return user.get();
        //}
        //return null;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User loginUser(String username, String password) {
        User temp = this.userRepository.findByUsername(username);
        if (temp == null) throw new UserNotFoundException(username);
        if (temp.getPassword().equals(password)) {
            temp.setStatus(UserStatus.ONLINE);
            temp.setToken(UUID.randomUUID().toString());
            userRepository.save(temp);
            log.debug("User {} logged in!", username);
            return temp;
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
        userRepository.save(temp);
        return "logout successful!";
    }

    public User createUser(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()) != null){ throw new UserAlreadyExistsException(newUser.getUsername()); }
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreationDate(new Date());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public Boolean validateToken(String token) {
        return this.userRepository.findByToken(token) != null;
    }

    public void updateUser(User user, User newUser) {
        if (newUser.getUsername() != null) {
            user.setUsername(newUser.getUsername());
        }
        if (newUser.getBirthDay() != null){
            user.setBirthDay(newUser.getBirthDay());
        }
        userRepository.save(user);
    }
}