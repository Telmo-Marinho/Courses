package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);

    private static User getUser() {
        User user = new User();
        user.setUsername("teste");
        user.setCart(getCart(user));
        return user;
    }

    private static Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(getItem().orElse(null));
        return cart;
    }

    private static Optional<Item> getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal(7.7));
        return Optional.of(item);
    }

    private static List<UserOrder> getUserOrders() {
        UserOrder userOrder = UserOrder.createFromCart(getUser().getCart());
        return Lists.list(userOrder);
    }

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        when(userRepo.findByUsername("teste")).thenReturn(getUser());
        when(orderRepo.findByUser(any())).thenReturn(getUserOrders());
    }

    @Test
    public void submit_order_returned() {
        final ResponseEntity<UserOrder> response = orderController.submit("teste");
        UserOrder userOrder = response.getBody();

        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
        assertNotNull(userOrder);
        assertEquals(userOrder.getUser().getUsername(), "teste");
        assertEquals(userOrder.getItems().size(), 1);
        assertEquals(new BigDecimal(7.7),userOrder.getTotal());
    }

    @Test
    public void get_user_order() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("teste");
        List<UserOrder> userOrders = response.getBody();

        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
        assertNotNull(userOrders);
        assertEquals( 1,userOrders.size());

        UserOrder userOrder = userOrders.get(0);
        assertEquals("teste",userOrder.getUser().getUsername());
        assertEquals(new BigDecimal(7.7),userOrder.getTotal());
        assertEquals(1,userOrder.getItems().size());
    }


    @Test
    public void get_wrong_user_order() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("teste2");
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void submit_order_wrong_user() {
        ResponseEntity<UserOrder> response = orderController.submit("teste2");
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }



}
