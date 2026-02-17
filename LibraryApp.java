import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/* ---------------- USER CLASS ---------------- */
class User implements Serializable {
    private String username;
    private String password;
    private String role; // "ADMIN" or "USER"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean login(String u, String p) {
        return username.equals(u) && password.equals(p);
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
}

/* ---------------- BORROWER CLASS ---------------- */
class Borrower implements Serializable {
    private String id; // 3-digit ID
    private String name;

    public Borrower(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}

/* ---------------- BORROW RECORD ---------------- */
class BorrowRecord implements Serializable {
    String borrowerId;
    LocalDate issueDate;
    LocalDate dueDate;

    public BorrowRecord(String borrowerId) {
        this.borrowerId = borrowerId;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(14);
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return borrowerId + " (Issued: " + issueDate + ", Due: " + dueDate + ")";
    }
}

/* ---------------- BOOK CLASS ---------------- */
class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    private ArrayList<BorrowRecord> borrowRecords = new ArrayList<>();

    public Book(int id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = copies;
        this.availableCopies = copies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean issueBook(String borrowerId) {
        if (availableCopies > 0) {
            availableCopies--;
            borrowRecords.add(new BorrowRecord(borrowerId));
            return true;
        }
        return false;
    }

    public double returnBook(String borrowerId) {
        for (int i = 0; i < borrowRecords.size(); i++) {
            if (borrowRecords.get(i).getBorrowerId().equals(borrowerId)) {
                BorrowRecord record = borrowRecords.remove(i);
                availableCopies++;
                double fine = 0.0;

                if (LocalDate.now().isAfter(record.getDueDate())) {
                    long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
                    fine = daysLate * 5.0; // ₹5 per day
                    System.out.println("→ Book returned LATE by " + daysLate + " day(s). Fine: ₹" + fine);
                } else {
                    System.out.println("→ Book returned on time. No fine.");
                }
                return fine;
            }
        }
        return -1; // Indicates not borrowed by this user
    }

    public void updateBook(String title, String author, int copies) {
        this.title = title;
        this.author = author;
        int currentlyBorrowed = borrowRecords.size();
        this.totalCopies = copies;
        this.availableCopies = copies - currentlyBorrowed;
    }

    public void display() {
        System.out.printf("ID: %04d | %-30s | %-25s | Avail: %2d/%2d%n",
                id, title, author, availableCopies, totalCopies);
    }

    public void showDetails(Map<String, Borrower> borrowerMap) {
        display();
        if (borrowRecords.isEmpty()) {
            System.out.println("   Not currently borrowed.");
        } else {
            System.out.println("   Borrowed records:");
            for (BorrowRecord br : borrowRecords) {
                Borrower borrower = borrowerMap.get(br.getBorrowerId());
                String borrowerInfo = (borrower != null) ? borrower.toString() : br.getBorrowerId() + " (Name unknown)";
                System.out.println("     • " + borrowerInfo + " | " + br.issueDate + " | Due: " + br.dueDate);
            }
        }
    }

    public List<BorrowRecord> getBorrowRecordsForBorrower(String borrowerId) {
        List<BorrowRecord> result = new ArrayList<>();
        for (BorrowRecord br : borrowRecords) {
            if (br.getBorrowerId().equals(borrowerId)) {
                result.add(br);
            }
        }
        return result;
    }
}

/* ---------------- BORROWER MANAGER ---------------- */
class BorrowerManager {
    private Map<String, Borrower> borrowers = new HashMap<>();
    private final String BORROWERS_FILE = "borrowers.dat";

    public BorrowerManager() {
        loadBorrowers();
    }

    private void loadBorrowers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BORROWERS_FILE))) {
            borrowers = (Map<String, Borrower>) ois.readObject();
        } catch (Exception e) {
            borrowers = new HashMap<>();
        }
    }

    private void saveBorrowers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BORROWERS_FILE))) {
            oos.writeObject(borrowers);
        } catch (Exception e) {
            System.out.println("Error saving borrowers: " + e.getMessage());
        }
    }

    public Borrower getOrCreateBorrower(String id, Scanner sc) {
        if (!borrowers.containsKey(id)) {
            System.out.print("New borrower! Enter name for ID " + id + ": ");
            String name = sc.nextLine().trim();
            Borrower newBorrower = new Borrower(id, name);
            borrowers.put(id, newBorrower);
            saveBorrowers();
            System.out.println("Borrower added: " + newBorrower);
        }
        return borrowers.get(id);
    }

    public Borrower getBorrower(String id) {
        return borrowers.get(id);
    }

    public Map<String, Borrower> getAllBorrowers() {
        return borrowers;
    }
}

/* ---------------- LIBRARY CLASS ---------------- */
class Library implements Serializable {
    private ArrayList<Book> books = new ArrayList<>();
    private final String FILE_NAME = "library.dat";

    public Library() {
        loadFromFile();
    }

    public void addBook(Book b) {
        books.add(b);
        saveToFile();
        System.out.println("→ Book added successfully.");
    }

    public void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        System.out.println("\n===== Library Books =====");
        for (Book b : books) {
            b.display();
        }
    }

    public void searchBooks(String keyword) {
        keyword = keyword.trim().toLowerCase();
        if (keyword.isEmpty()) {
            System.out.println("Enter search term.");
            return;
        }

        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword) ||
                    b.getAuthor().toLowerCase().contains(keyword)) {
                results.add(b);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matches for '" + keyword + "'.");
        } else {
            System.out.println("\nResults for '" + keyword + "':");
            for (Book b : results) {
                b.display();
            }
        }
    }

    public void viewBookDetails(int id, Map<String, Borrower> borrowerMap) {
        for (Book b : books) {
            if (b.getId() == id) {
                b.showDetails(borrowerMap);
                return;
            }
        }
        System.out.println("Book ID " + id + " not found.");
    }

    public void updateBook(int id, String title, String author, int copies) {
        for (Book b : books) {
            if (b.getId() == id) {
                b.updateBook(title, author, copies);
                saveToFile();
                System.out.println("→ Book updated.");
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void deleteBook(int id) {
        boolean removed = books.removeIf(b -> b.getId() == id);
        if (removed) {
            saveToFile();
            System.out.println("→ Book deleted.");
        } else {
            System.out.println("Book not found.");
        }
    }

    public void issueBook(int bookId, String borrowerId, BorrowerManager borrowerManager, Scanner sc) {
        borrowerManager.getOrCreateBorrower(borrowerId, sc);
        for (Book b : books) {
            if (b.getId() == bookId) {
                if (b.issueBook(borrowerId)) {
                    saveToFile();
                    System.out.println("→ Issued successfully. Due: " + LocalDate.now().plusDays(14));
                } else {
                    System.out.println("No copies available.");
                }
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void returnBook(int bookId, String borrowerId) {
        for (Book b : books) {
            if (b.getId() == bookId) {
                double fine = b.returnBook(borrowerId);
                if (fine >= 0) {
                    saveToFile();
                    System.out.println("→ Returned successfully.");
                } else {
                    System.out.println("This ID did not borrow this book.");
                }
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void showBooksBorrowedBy(String borrowerId, Map<String, Borrower> borrowerMap) {
        System.out.println("\nBooks borrowed by "
                + borrowerMap.getOrDefault(borrowerId, new Borrower(borrowerId, "Unknown")).toString() + ":");
        boolean foundAny = false;

        for (Book b : books) {
            List<BorrowRecord> records = b.getBorrowRecordsForBorrower(borrowerId);
            if (!records.isEmpty()) {
                foundAny = true;
                System.out.println("  Book #" + b.getId() + " - " + b.getTitle());
                for (BorrowRecord r : records) {
                    System.out.println("     • Issued: " + r.getIssueDate() + " | Due: " + r.getDueDate());
                }
            }
        }

        if (!foundAny) {
            System.out.println("   No books currently borrowed by this ID.");
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(books);
        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            books = (ArrayList<Book>) ois.readObject();
        } catch (Exception e) {
            books = new ArrayList<>();
        }
    }
}

/* ---------------- USER MANAGER ---------------- */
class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private final String USERS_FILE = "users.dat";

    public UserManager() {
        loadUsers();
        if (users.isEmpty()) {
            users.add(new User("admin", "admin123", "ADMIN"));
            saveUsers();
        }
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (ArrayList<User>) ois.readObject();
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            System.out.println("Users save error: " + e.getMessage());
        }
    }

    public User authenticate(Scanner sc) {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        for (User u : users) {
            if (u.login(username, password)) {
                System.out.println("Welcome, " + username + "!");
                return u;
            }
        }
        System.out.println("Invalid login.");
        return null;
    }

    public void registerNewUser(Scanner sc) {
        System.out.print("New username: ");
        String username = sc.nextLine().trim();

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("Username already exists.");
                return;
            }
        }

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        users.add(new User(username, password, "USER"));
        saveUsers();
        System.out.println("Registered successfully. You can now login.");
    }
}

/* ---------------- MAIN APPLICATION ---------------- */
public class LibraryApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager();
        BorrowerManager borrowerManager = new BorrowerManager();
        Library library = new Library();
        User currentUser = null;

        System.out.println("=====================================");
        System.out.println("     Library Management System     ");
        System.out.println("=====================================");

        // Login / Register loop
        while (currentUser == null) {
            System.out.println("\n1. Login");
            System.out.println("2. Register (student)");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            int opt = getValidInt(sc);
            sc.nextLine();

            if (opt == 1) {
                currentUser = userManager.authenticate(sc);
            } else if (opt == 2) {
                userManager.registerNewUser(sc);
            } else if (opt == 3) {
                System.out.println("Goodbye!");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        // Main menu loop
        while (true) {
            System.out.println("\n----------------------------------------");
            System.out.println("Logged in: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
            System.out.println("----------------------------------------");

            if (currentUser.getRole().equals("ADMIN")) {
                showAdminMenu();
            } else {
                showUserMenu();
            }

            System.out.print("\nChoice: ");
            int choice = getValidInt(sc);
            sc.nextLine();

            if (currentUser.getRole().equals("ADMIN")) {
                handleAdminChoice(choice, library, borrowerManager, sc);
            } else {
                handleUserChoice(choice, library, borrowerManager, sc);
            }

            if (choice == 0) {
                System.out.println("\nLogged out successfully.");
                currentUser = null;
                continue;
            }
            if (choice == 99) {
                System.out.println("\nThank you! Visit again.");
                break;
            }
        }

        sc.close();
    }

    private static void showAdminMenu() {
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Books");
        System.out.println("4. View Book Details");
        System.out.println("5. Issue Book");
        System.out.println("6. Return Book");
        System.out.println("7. View Borrower's Books");
        System.out.println("8. Update Book");
        System.out.println("9. Delete Book");
        System.out.println("0. Logout");
        System.out.println("99. Exit Program");
    }

    private static void showUserMenu() {
        System.out.println("1. View All Books");
        System.out.println("2. Search Books");
        System.out.println("3. View Book Details");
        System.out.println("4. Borrow Book");
        System.out.println("5. Return Book");
        System.out.println("6. My Borrowed Books");
        System.out.println("0. Logout");
        System.out.println("99. Exit Program");
    }

    private static void handleAdminChoice(int choice, Library lib, BorrowerManager borrowerManager, Scanner sc) {
        switch (choice) {
            case 1:
                System.out.print("Book ID: ");
                int id = getValidInt(sc);
                sc.nextLine();
                System.out.print("Title: ");
                String title = sc.nextLine().trim();
                System.out.print("Author: ");
                String author = sc.nextLine().trim();
                System.out.print("Total copies: ");
                int copies = getValidInt(sc);
                lib.addBook(new Book(id, title, author, copies));
                break;

            case 2:
                lib.viewBooks();
                break;

            case 3:
                System.out.print("Search (title/author): ");
                lib.searchBooks(sc.nextLine().trim());
                break;

            case 4:
                System.out.print("Book ID: ");
                lib.viewBookDetails(getValidInt(sc), borrowerManager.getAllBorrowers());
                break;

            case 5:
                System.out.print("Book ID: ");
                int bookId = getValidInt(sc);
                sc.nextLine();
                System.out.print("Borrower ID (3 digits): ");
                String borrowerId = sc.nextLine().trim();
                if (isValidId(borrowerId)) {
                    lib.issueBook(bookId, borrowerId, borrowerManager, sc);
                }
                break;

            case 6:
                System.out.print("Book ID: ");
                bookId = getValidInt(sc);
                sc.nextLine();
                System.out.print("Borrower ID (3 digits): ");
                borrowerId = sc.nextLine().trim();
                if (isValidId(borrowerId))
                    lib.returnBook(bookId, borrowerId);
                break;

            case 7:
                System.out.print("Borrower ID (3 digits): ");
                borrowerId = sc.nextLine().trim();
                if (isValidId(borrowerId))
                    lib.showBooksBorrowedBy(borrowerId, borrowerManager.getAllBorrowers());
                break;

            case 8:
                System.out.print("Book ID: ");
                id = getValidInt(sc);
                sc.nextLine();
                System.out.print("New Title: ");
                title = sc.nextLine().trim();
                System.out.print("New Author: ");
                author = sc.nextLine().trim();
                System.out.print("New Copies: ");
                copies = getValidInt(sc);
                lib.updateBook(id, title, author, copies);
                break;

            case 9:
                System.out.print("Delete Book ID: ");
                lib.deleteBook(getValidInt(sc));
                break;

            case 0:
            case 99:
                break;

            default:
                System.out.println("Invalid option.");
        }
    }

    private static void handleUserChoice(int choice, Library lib, BorrowerManager borrowerManager, Scanner sc) {
        switch (choice) {
            case 1:
                lib.viewBooks();
                break;

            case 2:
                System.out.print("Search (title/author): ");
                lib.searchBooks(sc.nextLine().trim());
                break;

            case 3:
                System.out.print("Book ID: ");
                lib.viewBookDetails(getValidInt(sc), borrowerManager.getAllBorrowers());
                break;

            case 4:
                System.out.print("Book ID: ");
                int bookId = getValidInt(sc);
                sc.nextLine();
                System.out.print("Your Borrower ID (3 digits): ");
                String borrowerId = sc.nextLine().trim();
                if (isValidId(borrowerId)) {
                    lib.issueBook(bookId, borrowerId, borrowerManager, sc);
                }
                break;

            case 5:
                System.out.print("Book ID: ");
                bookId = getValidInt(sc);
                sc.nextLine();
                System.out.print("Your Borrower ID (3 digits): ");
                borrowerId = sc.nextLine().trim();
                if (isValidId(borrowerId))
                    lib.returnBook(bookId, borrowerId);
                break;

            case 6:
                System.out.print("Your Borrower ID (3 digits): ");
                String myId = sc.nextLine().trim();
                if (isValidId(myId))
                    lib.showBooksBorrowedBy(myId, borrowerManager.getAllBorrowers());
                break;

            case 0:
            case 99:
                break;

            default:
                System.out.println("Invalid option.");
        }
    }

    private static boolean isValidId(String id) {
        if (id == null || id.length() != 3 || !id.matches("\\d{3}")) {
            System.out.println("Borrower ID must be exactly 3 digits (example: 101, 007).");
            return false;
        }
        return true;
    }

    private static int getValidInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.next(); // discard invalid input
        }
        int value = sc.nextInt();
        return value;
    }
}