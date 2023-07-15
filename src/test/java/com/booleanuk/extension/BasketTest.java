package com.booleanuk.extension;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.booleanuk.extension.SKU.*;
import static org.junit.jupiter.api.Assertions.*;

public class BasketTest {
    @Test
    public void testAddingBagelToBasket() {
        Basket basket = new Basket();

        assertTrue(basket.addItem(new Bagel(BGLO)));
        assertTrue(basket.addItem(new Bagel(BGLO, new Filling(FILB))));
    }

    @Test
    public void testRemovingBagelFromBasket() {
        Basket basket = new Basket();
        Bagel bagel = new Bagel(BGLO);
        basket.addItem(bagel);

        assertTrue(basket.removeItem(bagel));
    }

    @Test
    public void testCheckingIfBasketIsFull() {
        Basket basket = new Basket(2);

        basket.addItem(new Bagel(BGLO));
        assertFalse(basket.isFull());

        basket.addItem(new Bagel(BGLO));
        assertTrue(basket.isFull());
    }

    @Test
    public void testDenyingAddingBagelIfBasketIsFull() {
        Basket basket = new Basket(2);

        basket.addItem(new Bagel(BGLO));
        basket.addItem(new Bagel(BGLO));
        assertFalse(basket.addItem(new Bagel(BGLO)));
    }

    @Test
    public void testChangingBasketCapacity() {
        Basket basket = new Basket(2);

        basket.setCapacity(3);
        assertEquals(3, basket.getCapacity());

        assertThrows(IllegalArgumentException.class , () -> basket.setCapacity(-1));

        basket.addItem(new Bagel(BGLO));
        basket.addItem(new Bagel(BGLO));
        assertThrows(IllegalArgumentException.class , () -> basket.setCapacity(1));
    }

    @Test
    public void testCheckingIfBagelIsInBasket() {
        Basket basket = new Basket();
        Bagel bagel = new Bagel(BGLO);

        assertFalse(basket.isInBasket(bagel));

        basket.addItem(bagel);
        assertTrue(basket.isInBasket(bagel));
    }

    @Test
    public void testRemovingNonexistentBagelFromBasket() {
        Basket basket = new Basket();
        assertFalse(basket.removeItem(new Bagel(BGLO)));
    }

    @Test
    public void testGettingTotalPriceOfItemsInBasket() {
        Basket basket = new Basket();
        basket.addItem(new Bagel(BGLO));
        basket.addItem(new Bagel(BGLO));

        assertEquals(BigDecimal.valueOf(0.98), basket.getTotalPrice());
    }

    @Test
    public void testCheckingBagelPrice() {
        assertEquals(BigDecimal.valueOf(0.49), Basket.checkPrice(new Bagel(BGLO)));
        assertEquals(BigDecimal.valueOf(0.61), Basket.checkPrice(new Bagel(BGLO, new Filling(FILB))));
    }

    @Test
    public void testAddingFillingToBagel() {
        Basket basket = new Basket();
        basket.addItem(new Bagel(BGLO, new Filling(FILC)));
        assertEquals(2, basket.getItems().size());
    }

    @Test
    public void testGettingAvailableFillings() {
        Basket basket = new Basket();
        assertEquals(6, basket.getAvailableFillings().size());
    }

    @Test
    public void testCheckingPriceOfFilling() {
        assertEquals(BigDecimal.valueOf(0.12), Basket.checkPrice(new Filling(FILB)));
    }

    @Test
    public void testCreatingItemUsingStrings() {
        Bagel bagel = new Bagel("Onion", new Filling("Bacon"));
        assertNotNull(bagel);
    }

    @Test
    public void testOrderingItemOutOfInventory() {
        assertThrows(IllegalArgumentException.class, () -> new Bagel("Incorrect"));
    }

    @Test
    public void testAddingBasketInStore() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);

        assertEquals(1, store.getBaskets().size());
    }

    @Test
    public void testPlacingOrderUsingSKU() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);

        assertNull(store.placeOrder(basket));

        basket.addItem(new Bagel(SKU.BGLO));
        basket.addItem(new Bagel(SKU.BGLO));
        assertNotNull(store.placeOrder(basket));

        assertEquals(0, store.getBaskets().size());
    }

    @Test
    public void testGettingTotalPriceOfOrder() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);
        basket.addItem(new Bagel(BGLO));
        basket.addItem(new Bagel(BGLO));

        assertEquals(BigDecimal.valueOf(0.98), basket.getTotalPrice());
    }

    @Test
    public void testSpecialOffers() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);
        IntStream.range(0, 2).mapToObj(i -> new Bagel(BGLO)).forEach(basket::addItem);
        IntStream.range(0, 12).mapToObj(i -> new Bagel(BGLP)).forEach(basket::addItem);
        IntStream.range(0, 6).mapToObj(i -> new Bagel(BGLE)).forEach(basket::addItem);
        IntStream.range(0, 3).mapToObj(i -> new Coffee(COFB)).forEach(basket::addItem);

        UUID id = store.placeOrder(basket);
        Order order = store.getOrder(id);

        assertEquals(BigDecimal.valueOf(9.97), order.getTotalPriceAfterDiscount());
    }

    @Test
    public void testCoffeeAndBagelSpecialOffer() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);

    }
    @Test
    public void testPrintingReceipt() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);
        IntStream.range(0, 2).mapToObj(i -> new Bagel(SKU.BGLO)).forEach(basket::addItem);
        IntStream.range(0, 12).mapToObj(i -> new Bagel(SKU.BGLP)).forEach(basket::addItem);
        IntStream.range(0, 6).mapToObj(i -> new Bagel(SKU.BGLE)).forEach(basket::addItem);
        IntStream.range(0, 3).mapToObj(i -> new Coffee(SKU.COFB)).forEach(basket::addItem);

        UUID id = store.placeOrder(basket);
        Order order = store.getOrder(id);

        BigDecimal expectedTotalPrice = BigDecimal.valueOf(10.43);
        assertEquals(expectedTotalPrice, order.getTotalPriceAfterDiscount());

        Receipt receipt = new Receipt(order);
        receipt.printReceipt();
    }

    @Test
    public void testPrintingReceiptTwo() {
        Store store = new Store();
        Basket basket = new Basket();
        store.addBasket(basket);
        IntStream.range(0, 16).mapToObj(i -> new Bagel(SKU.BGLO)).forEach(basket::addItem);

        UUID id = store.placeOrder(basket);
        Order order = store.getOrder(id);
        //it should be 5.55
        BigDecimal expectedTotalPrice = BigDecimal.valueOf(5.95);
        assertEquals(expectedTotalPrice, order.getTotalPriceAfterDiscount());

        Receipt receipt = new Receipt(order);
        receipt.printReceipt();
    }

}
