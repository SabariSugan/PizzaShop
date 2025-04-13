import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

class Menu {
    private String[] pizzaMenu = {"Cheese Burst Pizza", "Veggie Pizza", "Paneer Pizza", "Pepperoni Pizza"};
    private double[] pizzaPrices = {250.0, 150.0, 200.0, 400.0};
    private String[] toppingMenu = {"Mushrooms", "Onions", "Bell Peppers", "Olives", "Bacon"};
    private double[] toppingPrices = {20.0, 30.0, 25.0, 10.0, 50.0};

    public void displayMenu() {
        System.out.println("--- Pizza Menu ---");
        for (int i = 0; i < pizzaMenu.length; i++) {
            System.out.printf("%d. %s - INR %.2f%n", i + 1, pizzaMenu[i], pizzaPrices[i]);
        }
    }

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

    public Vector<Topping> getToppings() {
        return toppings;
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

    public Vector<PizzaOrder> getOrders() {
        return orders;
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
    private Connection connection;

    public Shop(Scanner scanner) {
        this.scanner = scanner;
        connectToDatabase();
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/PizzaShopDB";
        String user = "root";
        String pass = "";
        try {
            connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomerOrderToDatabase(Customer customer) {
        try {
            String customerQuery = "INSERT INTO Orders (Name, Phone, Email, PizzaName, Size, Quantity, TotalAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement customerStmt = connection.prepareStatement(customerQuery);
            for (PizzaOrder order : orderManager.getOrders()) {
                customerStmt.setString(1, customer.getName());
                customerStmt.setString(2, customer.getPhone());
                customerStmt.setString(3, customer.getEmail());
                customerStmt.setString(4, order.getPizza().getName());
                customerStmt.setString(5, order.getPizza().getSizeAsString());
                customerStmt.setInt(6, order.getQuantity());
                customerStmt.setDouble(7, order.getTotal());
                customerStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            System.out.print("Select a pizza by number or enter 0 to exit: ");
            int pizzaChoice = scanner.nextInt();
            if (pizzaChoice == 0) {
                break;
            }
            if (pizzaChoice < 1 || pizzaChoice > menu.getPizzaCount()) {
                System.out.println("Invalid pizza choice. Please try again.");
                continue;
            }

            int size = 0;
            while (size < 1 || size > 3) {
                System.out.print("Enter pizza size:\n1. Small\n2. Medium\n3. Large\n");
                size = scanner.nextInt();
                if (size < 1 || size > 3) {
                    System.out.println("Invalid size choice. Please try again.");
                }
            }

            int quantity = -1;
            while (quantity <= 0) {
                System.out.print("Enter quantity: ");
                quantity = scanner.nextInt();
                if (quantity <= 0) {
                    System.out.println("Invalid quantity. Please enter a valid quantity.");
                }
            }

            totalBill += orderManager.processOrder(pizzaChoice, size, quantity, menu, scanner);
        }

        System.out.println("Your total for this order: INR " + totalBill);

        // Take customer details and validate
        System.out.print("Enter your name: ");
        String name = validateNameInput();

        System.out.print("Enter your phone number: ");
        String phone = validatePhoneInput();

        System.out.print("Enter your email: ");
        String email = validateEmailInput();

        Customer customer = new Customer(name, phone, email);
        saveCustomerOrderToDatabase(customer);
        System.out.println("Order details saved to the database.");
        return totalBill;
    }

    public String validatePhoneInput() {
        while (true) {
            String phone = scanner.nextLine();
            if (phone.matches("\\d{10}")) {
                return phone;
            } else {
                System.out.print("Invalid phone number. Enter again (10 digits): ");
            }
        }
    }

    public String validateNameInput() {
        while (true) {
            String name = scanner.nextLine();
            if (name.matches("[a-zA-Z\\s]+")) {
                return name;
            } else {
                System.out.print("Invalid name. Enter again (no numbers or special characters): ");
            }
        }
    }

    public String validateEmailInput() {
        while (true) {
            String email = scanner.nextLine();
            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return email;
            } else {
                System.out.print("Invalid email format. Enter again: ");
            }
        }
    }
}

public class PizzaShop18028V3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Shop shop = new Shop(scanner);

        if (shop.login()) {
            double totalBill = shop.runShop();
            System.out.println("Thank you for your order! Total Bill: INR " + totalBill);
        } else {
            System.out.println("Invalid credentials. Exiting...");
        }
    }
}
