package org.route;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public void displayMenu()
    {
        System.out.println("========== NILEMART ==========");
        System.out.println("1. Show products");
        System.out.println("2. Create a cart");
        System.out.println("3. Add a product to a cart");
        System.out.println("4. Show a cart");
        System.out.println("5. Check out a cart");
        System.out.println("0. Exit");
    }

    void main()
    {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);

        try
        {
            Map<Integer, ShoppingCart> carts = new HashMap<>();

            int nextCartNumber = 1;

            ProductService productService = context.getBean(ProductService.class);

            CheckoutService checkoutService = context.getBean(CheckoutService.class);

            Scanner input = new Scanner(System.in);

            boolean running = true;

            while (running)
            {
                displayMenu();
                System.out.println("PLEASE ENTER YOUR CHOICE : ");
                int choice = input.nextInt();
                input.nextLine();

                try {
                    switch (choice)
                    {
                        case 0: {
                            running = false;
                            System.out.println("THANK YOU FOR USING NILEMART , HAVE A NICE DAY !");
                            break;
                        }
                        case 1: {
                            System.out.println("===== Product Catalog =====");

                            for (Product product : productService.findAll()) {
                                System.out.println(product.getSku() + " | "
                                + product.getName() + " | " + product.getPrice() + " EGP | "
                                + "Stock: " + product.getStockQuantity());
                            }
                            break;
                        }
                        case 2: {
                            System.out.print("Enter customer name: ");
                            String name = input.nextLine();

                            System.out.print("Enter phone: ");
                            String phone = input.nextLine();

                            System.out.print("Enter email (optional): ");
                            String email = input.nextLine();

                            ShoppingCart cart = context.getBean(ShoppingCart.class);

                            cart.setCartNumber(nextCartNumber);
                            cart.setCustomer(new Customer(name, phone, email));

                            carts.put(nextCartNumber, cart);

                            System.out.println("Cart created successfully!");
                            System.out.println("Cart Number: " + nextCartNumber);

                            nextCartNumber++;
                            break;
                        }
                        case 3: {
                            System.out.print("Enter cart number: ");
                            int cartNumber = input.nextInt();
                            input.nextLine();

                            ShoppingCart selectedCart = carts.get(cartNumber);

                            if (selectedCart == null) {
                                System.out.println("Cart not found!");
                                break;
                            }

                            System.out.print("Enter product SKU: ");
                            String sku = input.nextLine();

                            System.out.print("Enter quantity: ");
                            int quantity = input.nextInt();
                            input.nextLine();

                            selectedCart.addItem(sku, quantity);

                            System.out.println("Item added successfully!");
                            break;
                        }
                        case 4: {
                            System.out.print("Enter cart number: ");
                            int number = input.nextInt();
                            input.nextLine();

                            ShoppingCart showCart = carts.get(number);

                            if (showCart == null) {
                                System.out.println("Cart not found!");
                                break;
                            }

                            System.out.println("===== Shopping Cart =====");

                            if (showCart.isEmpty()) {
                                System.out.println("Cart is empty.");
                            }
                            else {
                                for (var entry : showCart.getLines().entrySet()) {
                                    Product product = productService.findBySku(entry.getKey());

                                    System.out.println(product.getName() + " | Quantity: " + entry.getValue()
                                     + " | Price: " + productService.getPrice(entry.getKey(), entry.getValue())
                                      + " EGP");
                                }

                                System.out.println("Subtotal: " + showCart.subtotal() + " EGP");
                            }
                            break;
                        }
                        case 5: {
                            System.out.print("Enter cart number: ");
                            int checkoutCartNumber = input.nextInt();
                            input.nextLine();

                            ShoppingCart cartToCheckout = carts.get(checkoutCartNumber);

                            if (cartToCheckout == null) {
                                System.out.println("Cart not found!");
                                break;
                            }

                            System.out.println("Available Governorates:");
                            for (Governorate g : Governorate.values())
                            {
                                System.out.println(g);
                            }

                            System.out.print("Enter governorate: ");
                            String governorateName = input.nextLine().trim().toUpperCase();

                            try {
                                Governorate governorate = Governorate.valueOf(governorateName);

                                Order order = checkoutService.placeOrder(cartToCheckout, governorate);

                                System.out.println("===== Order Confirmation =====");
                                System.out.println("Order Number: " + order.orderNumber());
                                System.out.println("Subtotal: " + order.subtotal());
                                System.out.println("VAT: " + order.vat());
                                System.out.println("Shipping: " + order.shippingFee());
                                System.out.println("Total: " + order.total() + " EGP");

                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid governorate!");
                            }
                            break;
                        }
                        default: {
                            System.out.println("SORRY CHOOSE FROM 0 TO 5");
                            choice = input.nextInt();
                            input.nextLine();
                        }
                    }
                }
                catch (StoreException message)
                {
                    System.out.println("ERROR : " + message.getMessage());
                }
            }
        }
        finally
        {
            context.close();
        }

    }
}
