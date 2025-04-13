import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

class Menu {
    private String[] pizzaMenu = {"Cheese Burst Pizza", "Veggie Pizza", "Paneer Pizza", "Pepperoni Pizza"};
    private double[] pizzaPrices = {250.0, 150.0, 200.0, 400.0};
    private String[] toppingMenu = {"Mushrooms", "Onions", "Bell Peppers", "Olives", "Bacon"};
    private double[] toppingPrices = {20.0, 30.0, 25.0, 10.0, 50.0};

    public String[] getPizzaMenu() {
        return pizzaMenu;
    }

    public double getPizzaPrice(int index) {
        return pizzaPrices[index];
    }

    public String[] getToppingMenu() {
        return toppingMenu;
    }

    public double getToppingPrice(int index) {
        return toppingPrices[index];
    }

    public int getPizzaCount() {
        return pizzaMenu.length;
    }

    public void displayMenu() {
        System.out.println("--- Pizza Menu ---");
        for (int i = 0; i < pizzaMenu.length; i++) {
            System.out.printf("%d. %s - INR %.2f%n", i + 1, pizzaMenu[i], pizzaPrices[i]);
        }
    }
}

class Pizza {
    private String name;
    private double price;
    private int size;

    public Pizza(String name, double price, int size) {
        this.name = name;
        this.price = price;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public String getSizeAsString() {
        switch (size) {
            case 1: return "Small";
            case 2: return "Medium";
            case 3: return "Large";
            default: return "Unknown";
        }
    }
}

class Topping {
    private String name;
    private double price;

    public Topping(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

class PizzaOrder {
    private Pizza pizza;
    private int size;
    private int quantity;
    private Vector<Topping> toppings;
    private double total;

    public PizzaOrder(Pizza pizza, int size, int quantity, Vector<Topping> toppings) {
        this.pizza = pizza;
        this.size = size;
        this.quantity = quantity;
        this.toppings = toppings;
        this.total = calculateTotal();
    }

    public double calculateTotal() {
        double toppingsCost = 0;
        for (Topping topping : toppings) {
            toppingsCost += topping.getPrice();
        }
        return (pizza.getPrice() + toppingsCost) * quantity;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }
}

class OrderManager {
    private final Vector<PizzaOrder> orders = new Vector<>();

    public double processOrder(int pizzaChoice, int size, int quantity, Menu menu, Scanner scanner) {
        String pizzaName = menu.getPizzaMenu()[pizzaChoice - 1];
        double pizzaPrice = menu.getPizzaPrice(pizzaChoice - 1);
        Pizza pizza = new Pizza(pizzaName, pizzaPrice, size);
        Vector<Topping> toppings = addToppingsToOrder(menu, scanner);
        PizzaOrder order = new PizzaOrder(pizza, size, quantity, toppings);

        orders.add(order);
        return order.getTotal();
    }

    private Vector<Topping> addToppingsToOrder(Menu menu, Scanner scanner) {
        Vector<Topping> selectedToppings = new Vector<>();

        while (true) {
            System.out.println("Choose a topping (or type 0 to finish):");
            for (int i = 0; i < menu.getToppingMenu().length; i++) {
                System.out.printf("%d. %s - INR %.2f%n", i + 1, menu.getToppingMenu()[i], menu.getToppingPrice(i));
            }

            try {
                int toppingChoice = scanner.nextInt();
                scanner.nextLine();
                if (toppingChoice == 0) break;
                if (toppingChoice > 0 && toppingChoice <= menu.getToppingMenu().length) {
                    Topping topping = new Topping(menu.getToppingMenu()[toppingChoice - 1], menu.getToppingPrice(toppingChoice - 1));
                    selectedToppings.add(topping);
                } else {
                    System.out.println("Invalid topping choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                scanner.nextLine();
            }
        }
        return selectedToppings;
    }

    public void printOrderSummary() {
        for (PizzaOrder order : orders) {
            System.out.printf("Pizza: %s | Size: %s | Quantity: %d | Total Price: INR %.2f%n",
                order.getPizza().getName(),
                order.getPizza().getSizeAsString(),
                order.getQuantity(),
                order.getTotal());
        }
    }

    public int getOrderCount() {
        return orders.size();
    }
}

class Customer {
    private String name;
    private String phone;
    private String email;

    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}

class Shop {
    private final String USERNAME = "pizza";
    private final String PASSWORD = "pizza";
    private final Menu menu = new Menu();
    private final OrderManager orderManager = new OrderManager();
    private final Scanner scanner;

    public Shop(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean login() {
        System.out.print("--- Login ---\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        return USERNAME.equals(username) && PASSWORD.equals(scanner.nextLine());
    }

    public double runShop() {
        double totalBill = 0;
        menu.displayMenu();
        while (true) {
            int choice = getUserChoice();
            if (choice == -1) {
                System.out.println("Exiting the program. Thank you for visiting Pizza Palace!");
                break;
            }
            if (choice >= 1 && choice <= menu.getPizzaCount()) {
                int size = getPizzaSize();
                int quantity = getPizzaQuantity();
                totalBill += orderManager.processOrder(choice, size, quantity, menu, scanner);
            } else {
                System.out.println("Invalid choice. Please select a number between 1 and " + menu.getPizzaCount() + ".");
            }
        }
        return totalBill;
    }

    private int getUserChoice() {
        while (true) {
            System.out.print("Select a pizza by number or enter 6 to exit: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 6) return -1;
                return choice;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    private int getPizzaSize() {
        while (true) {
            System.out.println("Choose pizza size:");
            System.out.println("1. Small\n2. Medium\n3. Large");
            System.out.print("Enter your choice (1, 2, or 3): ");
            try {
                int size = scanner.nextInt();
                scanner.nextLine();
                if (size >= 1 && size <= 3) return size;
                System.out.println("Invalid size! Please choose 1, 2, or 3.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    private int getPizzaQuantity() {
        while (true) {
            System.out.print("Enter the quantity: ");
            try {
                int quantity = scanner.nextInt();
                scanner.nextLine();
                if (quantity > 0) return quantity;
                System.out.println("Quantity must be a positive integer.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid quantity.");
                scanner.next();
            }
        }
    }

    public void printOrderSummary() {
        orderManager.printOrderSummary();
    }

    public void printBill(Customer customer, double totalBill) {
        System.out.printf("\n--- Bill ---\nCustomer Name: %s\nPhone Number: %s\nEmail: %s\nTotal Amount: INR %.2f%n",
                customer.getName(), customer.getPhone(), customer.getEmail(), totalBill);
    }

    public Customer collectCustomerDetails() {
        String name = getCustomerName();
        String phone = getCustomerPhone();
        String email = getCustomerEmail();
        return new Customer(name, phone, email);
    }

    private String getCustomerName() {
        while (true) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            if (name.matches("^[a-zA-Z ]+$")) {
                return name;
            } else {
                System.out.println("Name cannot contain numbers. Please enter a valid name.");
            }
        }
    }

    private String getCustomerPhone() {
        while (true) {
            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine();
            if (phone.matches("\\d{10}")) {
                return phone;
            } else {
                System.out.println("Invalid phone number! Please enter a valid 10-digit phone number.");
            }
        }
    }

    private String getCustomerEmail() {
        while (true) {
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            if (email.matches("^[\\w-]+(?:\\.[\\w-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")) {
                return email;
            } else {
                System.out.println("Invalid email format! Please enter a valid email.");
            }
        }
    }
}

public class PizzaShop18028V2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Shop shop = new Shop(scanner);

        System.out.println("Welcome to Pizza Palace!");

        if (!shop.login()) {
            System.out.println("Invalid credentials! Exiting...");
            return;
        }

        double totalBill = shop.runShop();
        if (totalBill > 0) {
            Customer customer = shop.collectCustomerDetails();
            shop.printOrderSummary();
            shop.printBill(customer, totalBill);
        }
    }
}
