package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs19.exceptions.PasswordNotValidException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Iterator;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");

        User createdUser = userService.createUser(testUser);

        Assert.assertNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(), UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByUsername(createdUser.getUsername()));
    }

    @Test
    public void getUsers() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        Iterable<User> users = userService.getUsers();
        Iterator<User> iter = users.iterator();
        Assert.assertEquals(iter.next(), testUser);
    }

    @Test
    public void loginValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User local = userService.loginUser("testUsername", "testPassword");
        Assert.assertEquals(local, testUser);
        Assert.assertEquals(local.getStatus(),UserStatus.ONLINE);
        Assert.assertNotNull(local.getToken());
    }


    @Test(expected = UserNotFoundException.class)
    public void loginInvalidUsername() {
        userRepository.deleteAll();
        userService.loginUser("User", "testPassword");
    }

    @Test(expected = PasswordNotValidException.class)
    public void loginInvalidPassword() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        userService.loginUser("testUsername", "wrongPw");
    }

    @Test
    public void logoutValidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        userService.loginUser("testUsername", "testPassword");
        userService.logoutUser(userRepository.findByUsername("testUsername").getToken());
        Assert.assertNull(userRepository.findByUsername("testUsername").getToken());
        Assert.assertEquals(userRepository.findByUsername("testUsername").getStatus(), UserStatus.OFFLINE);
    }


    @Test(expected = AuthenticationException.class)
    public void logoutInvalidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        userService.loginUser("testUsername", "testPassword");
        userService.logoutUser("ifthistokenworksitisabigsurprise");
    }

    @Test
    public void getValidUserId() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        User created = userService.createUser(testUser);

        Assert.assertEquals(userService.getUserById(created.getId()), created);
    }

    @Test(expected = UserNotFoundException.class)
    public void getInvalidUserId() {
        userRepository.deleteAll();
        Long testId = 1234L;
        userService.getUserById(testId);
    }

    @Test
    public void validateInvalidToken() {
        userRepository.deleteAll();
        Assert.assertFalse(userService.validateToken("ifthistokenworksitisabigsurprise"));
    }

    @Test
    public void validateValidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);
        User local = userService.loginUser("testUsername", "testPassword");

        Assert.assertTrue(userService.validateToken(local.getToken()));
    }

    @Test
    public void updateValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);
        User user = userService.loginUser("testUsername", "testPassword");

        User newUser = new User();
        newUser.setName("testName");
        newUser.setUsername("testUsernameUpdate");
        newUser.setBirthDay(new Date());
        newUser.setPassword("testPassword");

        userService.updateUser(user, newUser, user.getToken());

        user = userRepository.findById(user.getId()).orElse(null);
        Assert.assertEquals(user.getUsername(), newUser.getUsername());
        Assert.assertEquals(user.getBirthDay().toInstant(),newUser.getBirthDay().toInstant());
    }

    @Test(expected = AuthenticationException.class)
    public void updateUserInvalidToken() {
        userRepository.deleteAll();
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User some = new User();
        some.setName("someName");
        some.setUsername("someUsername");
        some.setBirthDay(new Date());
        some.setPassword("testPassword");
        userService.createUser(some);

        User local = userService.loginUser("someUsername", "testPassword");

        User newUser = new User();
        newUser.setName("testNameUpdate");
        newUser.setUsername("testUsernameUpdate");
        newUser.setBirthDay(new Date());
        newUser.setPassword("testPassword");

        userService.updateUser(userRepository.findByUsername("testUsername"), newUser, local.getToken());
    }

    @Test(expected = ConflictException.class)
    public void updateUserUsernameExisting() {
        userRepository.deleteAll();
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User some = new User();
        some.setName("someName");
        some.setUsername("someUsername");
        some.setBirthDay(new Date());
        some.setPassword("testPassword");
        userService.createUser(some);

        User local = userService.loginUser("testUsername", "testPassword");

        User updatedUser = new User();
        updatedUser.setName("testNameUpdate");
        updatedUser.setUsername("someUsername");
        updatedUser.setBirthDay(new Date());
        updatedUser.setPassword("testPassword");

        userService.updateUser(userRepository.findByUsername("testUsername"), updatedUser, local.getToken());
    }

}
