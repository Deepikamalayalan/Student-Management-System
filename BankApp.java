import com.mongodb.client.*;
import org.bson.Document;
import java.util.Scanner;
import static com.mongodb.client.model.Filters.eq;

public class BankApp {

    static Scanner scanner = new Scanner(System.in);
    static MongoCollection<Document> accounts;

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("bankDB");
        accounts = database.getCollection("accounts");

        while (true) {
            System.out.println("\n--- Bank Menu ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Show Balance");
            System.out.println("5. View All Accounts");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // clear newline

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> showBalance();
                case 5 -> viewAll();
                case 6 -> {
                    mongoClient.close();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    static void createAccount() {
        System.out.print("Enter holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();

        Document doc = new Document("name", name).append("balance", balance);
        accounts.insertOne(doc);
        System.out.println("Account created!");
    }

    static void deposit() {
        System.out.print("Enter holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();

        Document doc = accounts.find(eq("name", name)).first();
        if (doc != null) {
            double newBalance = doc.getDouble("balance") + amount;
            accounts.updateOne(eq("name", name), new Document("$set", new Document("balance", newBalance)));
            System.out.println("Deposit successful! New Balance: " + newBalance);
        } else {
            System.out.println("Account not found.");
        }
    }

    static void withdraw() {
        System.out.print("Enter holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        Document doc = accounts.find(eq("name", name)).first();
        if (doc != null) {
            double balance = doc.getDouble("balance");
            if (balance >= amount) {
                double newBalance = balance - amount;
                accounts.updateOne(eq("name", name), new Document("$set", new Document("balance", newBalance)));
                System.out.println("Withdrawal successful! New Balance: " + newBalance);
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    static void showBalance() {
        System.out.print("Enter holder name: ");
        String name = scanner.nextLine();

        Document doc = accounts.find(eq("name", name)).first();
        if (doc != null) {
            System.out.println("Current Balance: " + doc.getDouble("balance"));
        } else {
            System.out.println("Account not found.");
        }
    }

    static void viewAll() {
        System.out.println("\n--- All Accounts ---");
        for (Document doc : accounts.find()) {
            System.out.println("Name: " + doc.getString("name") + ", Balance: " + doc.getDouble("balance"));
        }
    }
}
