package org.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class ShoppingCart
{
    private final ProductService productService;
    private final int maxQuantity;
    private int cartNumber;
    private Customer customer;
    private final Map<String, Integer> lines = new HashMap<>();
    private final PricingService pricingService;

    public ShoppingCart(ProductService productService,
            @Value("${store.max-quantity-per-product}")int maxQuantity,PricingService pricingService)
    {
        this.productService = productService;
        this.maxQuantity = maxQuantity;
        this.pricingService = pricingService ;
    }

    public void setCartNumber(int cartNumber)
    {
        this.cartNumber = cartNumber;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public int getCartNumber()
    {
        return cartNumber;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public Map<String, Integer> getLines()
    {
        return lines;
    }

    public void addItem(String sku, int qty)
    {
        Product product = productService.findBySku(sku);

        if (qty <= 0)
        {
            throw new StoreException("Quantity must be greater than zero.");
        }

        if (product.getStockQuantity() == 0) {
            throw new StoreException("Product is out of stock.");
        }

        int currentQuantity = lines.getOrDefault(sku, 0);
        int newQuantity = currentQuantity + qty;

        if (newQuantity > maxQuantity) {
            throw new StoreException("Maximum quantity per product is " + maxQuantity);
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new StoreException("Not enough stock available.");
        }

        lines.put(sku, newQuantity);
    }

    public BigDecimal subtotal()
    {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : lines.entrySet()) {

            String sku = entry.getKey();
            int qty = entry.getValue();

            Product product = productService.findBySku(sku);

            BigDecimal linePrice = pricingService.getPrice(product, qty);

            total = total.add(linePrice);
        }

        return total;
    }

    public boolean isEmpty()
    {
        return lines.isEmpty();
    }

    public void clear()
    {
        lines.clear();
    }

}
