import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.regex.Pattern;

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
            } catch (InputMismatchException e) {
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
            } catch (InputMismatchException e) {
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
            } catch (InputMismatchException e) {
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

    public int getOrderCount() {
        return orderManager.getOrderCount();
    }

    public String getName() {
        while (true) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim(); 
            if (name.isEmpty() || name.contains(" ")) {
                System.out.println("Name should not contain spaces or be empty! Please enter a valid name.");
            } else if (name.matches(".*\\d.*")) {
                System.out.println("Name should not contain numbers! Please enter a valid name.");
            } else {
                return name;
            }
        }
    }

    public String getPhoneNumber() {
        while (true) {
            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine();
            if (phone.matches("\\d{10}")) return phone;
            System.out.println("Invalid phone number! Enter a valid 10-digit number.");
        }
    }

    public String getEmail() {
        while (true) {
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (Pattern.matches(emailRegex, email)) return email;
            System.out.println("Invalid email format! Please enter a valid email.");
        }
    }
}

class OrderManager {
    private final ArrayList<PizzaOrder> orders = new ArrayList<>();

    public double processOrder(int pizzaChoice, int size, int quantity, Menu menu, Scanner scanner) {
        String pizzaName = menu.getPizzaMenu().get(pizzaChoice - 1);
        double pizzaPrice = menu.getPizzaPrice(pizzaChoice - 1);
        Pizza pizza = new Pizza(pizzaName, pizzaPrice, size);
        ArrayList<Topping> toppings = addToppingsToOrder(menu, scanner);
        PizzaOrder order = new PizzaOrder(pizza, size, quantity, toppings);

        orders.add(order);
        return order.calculateTotal();
    }

    private ArrayList<Topping> addToppingsToOrder(Menu menu, Scanner scanner) {
        ArrayList<Topping> selectedToppings = new ArrayList<>();

        while (true) {
            System.out.println("Choose a topping (or type 0 to finish):");
            for (int i = 0; i < menu.getToppingMenu().size(); i++) {
                System.out.printf("%d. %s - INR %.2f%n", i + 1, menu.getToppingMenu().get(i), menu.getToppingPrice(i));
            }

            try {
                int toppingChoice = scanner.nextInt();
                scanner.nextLine();
                if (toppingChoice == 0) break;
                if (toppingChoice > 0 && toppingChoice <= menu.getToppingMenu().size()) {
                    selectedToppings.add(new Topping(menu.getToppingMenu().get(toppingChoice - 1), menu.getToppingPrice(toppingChoice - 1)));
                } else {
                    System.out.println("Invalid topping choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                scanner.next();
            }
        }
        return selectedToppings;
    }

    public void printOrderSummary() {
        System.out.println("\n--- Order Summary ---");
        for (PizzaOrder order : orders) {
            System.out.printf("Pizza: %s | Size: %s | Quantity: %d | Total Price: INR %.2f%n", 
                              order.getPizza().getName(), 
                              order.getPizza().getSizeAsString(), 
                              order.getQuantity(), 
                              order.calculateTotal());
        }
    }

    public int getOrderCount() {
        return orders.size();
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

    public String getSizeAsString() {
        switch (size) {
            case 1: return "Small";
            case 2: return "Medium";
            case 3: return "Large";
            default: return "Unknown";
        }
    }

    public double getPrice() {
        return price;
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
    private ArrayList<Topping> toppings;

    public PizzaOrder(Pizza pizza, int size, int quantity, ArrayList<Topping> toppings) {
        this.pizza = pizza;
        this.size = size;
        this.quantity = quantity;
        this.toppings = toppings;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public int getQuantity() {
        return quantity;
    }

    public double calculateTotal() {
        double total = pizza.getPrice() * quantity;
        for (Topping topping : toppings) {
            total += topping.getPrice();
        }
        return total;
    }
}

class Menu {
    private final ArrayList<String> pizzaMenu = new ArrayList<>();
    private final ArrayList<String> toppingMenu = new ArrayList<>();

    public Menu() {
        pizzaMenu.add("Margherita");
        pizzaMenu.add("Pepperoni");
        pizzaMenu.add("Veggie");

        toppingMenu.add("Olives");
        toppingMenu.add("Mushrooms");
        toppingMenu.add("Peppers");
    }

    public ArrayList<String> getPizzaMenu() {
        return pizzaMenu;
    }

    public ArrayList<String> getToppingMenu() {
        return toppingMenu;
    }

    public double getPizzaPrice(int index) {
        double[] prices = {200, 250, 300};
        return prices[index];
    }

    public double getToppingPrice(int index) {
        double[] prices = {20, 25, 30};
        return prices[index];
    }

    public int getPizzaCount() {
        return pizzaMenu.size();
    }

    public void displayMenu() {
        System.out.println("\n--- Pizza Menu ---");
        for (int i = 0; i < pizzaMenu.size(); i++) {
            System.out.printf("%d. %s - INR %.2f%n", i + 1, pizzaMenu.get(i), getPizzaPrice(i));
        }
    }
}

public class PizzaShop18028V1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Shop shop = new Shop(scanner);

        System.out.println("Welcome to Pizza Palace!");
        if (!shop.login()) {
            System.out.println("Invalid login credentials. Exiting.");
            return;
        }

        double totalBill = shop.runShop();

        if (shop.getOrderCount() > 0) {
            shop.printOrderSummary();

            String name = shop.getName();
            String phone = shop.getPhoneNumber();
            String email = shop.getEmail();
            Customer customer = new Customer(name, phone, email);

            shop.printBill(customer, totalBill);
        } else {
            System.out.println("No order placed.");
        }
    }
}
