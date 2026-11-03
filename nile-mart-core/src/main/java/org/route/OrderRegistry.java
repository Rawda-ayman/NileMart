package org.route;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;

@Component
public class OrderRegistry
{
    private final List<Order> orders = new ArrayList<>();
    private int orderCounter = 0;

    public void save(Order order)
    {
        orders.add(order);
    }

    public List<Order> findAll()
    {
        return orders;
    }

    public String generateOrderNumber()
    {
        orderCounter++;

        String date = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return String.format("NM-%s-%05d", date, orderCounter);
    }

    @PreDestroy
    public void printEndOfDaySummary()
    {
        BigDecimal revenue = BigDecimal.ZERO;

        for (Order order : orders)
        {
            revenue = revenue.add(order.total());
        }

        System.out.println("End of day: " + orders.size() + " orders , " + " revenue " + revenue + " EGP");
    }
}
