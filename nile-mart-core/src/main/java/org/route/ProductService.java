package org.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService
{

    private final ProductRepository repository;
    private final PricingService pricingService;


    public BigDecimal getPrice(String sku, int qty)
    {
        Product product = repository.findBySku(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new RuntimeException("Product isn't available."));
        return pricingService.getPrice(product, qty);
    }

    public List<Product> findAll()
    {
        return repository.findAll();
    }

    public Product findBySku(String sku)
    {
        return repository.findBySku(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new StoreException("Product isn't available."));
    }

    public void reduceStock(String sku, int qty)
    {
        Product product = findBySku(sku);

        if (product.getStockQuantity() < qty)
        {
            throw new StoreException("Not enough stock.");
        }

        product.setStockQuantity(product.getStockQuantity() - qty);
        repository.save(product);
    }





}
