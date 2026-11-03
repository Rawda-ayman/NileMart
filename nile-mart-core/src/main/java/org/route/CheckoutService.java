package org.route;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CheckoutService
{
    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> notificationSenders;

    public Order placeOrder(ShoppingCart cart, Governorate governorate) {

        // 1. Reject empty cart
        if (cart.isEmpty()) {
            throw new StoreException("Cannot checkout an empty cart");
        }

        // 2. Check stock for all products before changing any stock
        for (var entry : cart.getLines().entrySet()) {
            Product product = productService.findBySku(entry.getKey());

            if (product.getStockQuantity() < entry.getValue()) {
                throw new StoreException("Insufficient stock for product: " + product.getName());
            }
        }

        // 3. Calculate order amounts
        BigDecimal subtotal = cart.subtotal();

        BigDecimal vat = taxCalculator.calculateVat(subtotal);

        BigDecimal shippingFee = shippingQuoteService.quote(governorate, subtotal);

        BigDecimal total = subtotal.add(vat).add(shippingFee);

        // 4. Reduce stock for all products
        for (var entry : cart.getLines().entrySet())
        {
            productService.reduceStock(entry.getKey(), entry.getValue());
        }

        // 5. Create and save the order
        String orderNumber = orderRegistry.generateOrderNumber();

        Order order = new Order(orderNumber, cart.getCustomer(), governorate,
        new java.util.HashMap<>(cart.getLines()), subtotal, vat, shippingFee, total);

        orderRegistry.save(order);

        // 6. Send notifications
        String message = "Your order " + orderNumber + " has been placed successfully. Total: "
          + total + " EGP.";

        for (NotificationSender sender : notificationSenders)
        {
            sender.send(cart.getCustomer(), message);
        }

        // 7. Clear cart and return order
        cart.clear();

        return order;
    }

}
