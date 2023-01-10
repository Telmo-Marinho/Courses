package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private CartRepository cartRepo = mock(CartRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private static Optional<Item> getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal(7.7));
        return Optional.of(item);
    }


    private static Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(getItem().orElse(null));
        return cart;
    }
    private static User getUser() {
        User user = new User();
        user.setUsername("teste");
        user.setCart(getCart(user));
        return user;
    }

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
        when(userRepo.findByUsername("teste")).thenReturn(getUser());
        when(itemRepo.findById(1L)).thenReturn(getItem());
    }


    @Test
    public void if_invalid_username_then_error() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("");
        m.setItemId(1L);
        m.setQuantity(4);

        final ResponseEntity<Cart> response = cartController.addTocart(m);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void if_invalid_item_then_error() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("teste");
        m.setItemId(10L);
        m.setQuantity(3);

        final ResponseEntity<Cart> response = cartController.addTocart(m);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void remove_from_cart_with_invalid_user_then_error() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("");
        m.setItemId(1L);
        m.setQuantity(3);

        ResponseEntity<Cart> response = cartController.removeFromcart(m);
        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void add_new_item_to_cart_success() {
        int expectedQuantity = 5;
        BigDecimal expectedTotal = new BigDecimal(7.7).multiply(BigDecimal.valueOf(expectedQuantity));

        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("teste");
        m.setItemId(1L);
        m.setQuantity(4);

        final ResponseEntity<Cart> response = cartController.addTocart(m);
        Cart cart = response.getBody();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(cart);
        assertEquals("teste",cart.getUser().getUsername());
        assertEquals(expectedQuantity,cart.getItems().size());
        assertEquals(expectedTotal,cart.getTotal());
    }

    @Test
    public void if_remove_invalid_item_then_error() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("teste1");
        m.setItemId(0L);
        m.setQuantity(5);

        ResponseEntity<Cart> response = cartController.removeFromcart(m);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }

}
