package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void if_invalidPassword_notCreatedUser() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);
        assertEquals(response.getStatusCodeValue(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void if_invalidMatchingPassword_notCreatedUser() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("wrongPassword");

        final ResponseEntity<User> response = userController.createUser(r);
        assertEquals(response.getStatusCodeValue(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void if_find_user_by_wrong_username_then_error() {
        User mockUser = new User();
        mockUser.setUsername("teste1");
        when(userRepo.findByUsername("teste1")).thenReturn(mockUser);

        ResponseEntity<User> response = userController.findByUserName("teste2");
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_id_success() {
        long id = 1L;
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setUsername("teste1");
        when(userRepo.findById(id)).thenReturn(Optional.of(mockUser));

        ResponseEntity<User> response = userController.findById(id);
        User user = response.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
        Assert.assertEquals(1L,user.getId());
    }



}
