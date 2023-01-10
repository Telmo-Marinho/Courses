package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private static Item getItem(long id, String name) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        return item;
    }



    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        Item item1 = getItem(1L, "item_1");
        Item item2 = getItem(2L, "item_2");
        Item item3 = getItem(3L, "item_3");

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepo.findByName(item2.getName())).thenReturn(Lists.list(item2, item3));
        when(itemRepo.findAll()).thenReturn(Lists.list(item1, item2, item3));

    }
        @Test
        public void find_all_items_success() {
            final ResponseEntity<List<Item>> response = itemController.getItems();
            List<Item> items = response.getBody();

            assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
            assertNotNull(items);
            assertEquals(3, items.size());
        }

    @Test
    public void find_item_by_id_success() {
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        Item item = response.getBody();

        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        assertNotNull(item);
        assertEquals(1L, item.getId().longValue());
    }
    @Test
    public void find_item_by_wrong_id_then_error() {
        final ResponseEntity<Item> response = itemController.getItemById(10L);

        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }
}
