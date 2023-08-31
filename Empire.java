import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private boolean available;

    public Book(int bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.available = true;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

class User {
    private int userId;
    private String name;
    private int borrowedBooks;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.borrowedBooks = 0;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getBorrowedBooks() {
        return borrowedBooks;
    }

    public void incrementBorrowedBooks() {
        borrowedBooks++;
    }

    public void decrementBorrowedBooks() {
        borrowedBooks--;
    }
}

class Transaction {
    private int transactionId;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Transaction(int transactionId, int userId, int bookId, LocalDate borrowDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = null;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

class Library {
    private List<Book> books;
    private Map<Integer, User> users;
    private List<Transaction> transactions;
    private int transactionIdCounter;

    public Library() {
        this.books = new ArrayList<>();
        this.users = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.transactionIdCounter = 1;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public Book findBookById(int bookId) {
        for (Book book : books) {
            if (book.getBookId() == bookId) {
                return book;
            }
        }
        return null;
    }

    public User findUserById(int userId) {
        return users.get(userId);
    }

    public void registerStudent(User user) {
        users.put(user.getUserId(), user);
    }

    public boolean canBorrowBooks(User user) {
        return user.getBorrowedBooks() < 3;
    }

    public void borrowBook(Book book, int userId) {
        User user = users.get(userId);
        if (user != null && canBorrowBooks(user)) {
            if (book.isAvailable()) {
                book.setAvailable(false);
                LocalDate borrowDate = LocalDate.now();
                Transaction transaction = new Transaction(transactionIdCounter++, userId, book.getBookId(), borrowDate);
                transactions.add(transaction);
                user.incrementBorrowedBooks();
                System.out.println("Book \"" + book.getTitle() + "\" has been borrowed by " + user.getName());
            } else {
                System.out.println("Book is not available for borrowing.");
            }
        } else {
            System.out.println("User not found or has borrowed the maximum number of books.");
        }
    }

    public void returnBook(int bookId) {
        Book book = findBookById(bookId);
        if (book != null && !book.isAvailable()) {
            book.setAvailable(true);
            Transaction transaction = null;
            for (Transaction t : transactions) {
                if (t.getBookId() == bookId && t.getReturnDate() == null) {
                    transaction = t;
                    break;
                }
            }
            if (transaction != null) {
                transaction.setReturnDate(LocalDate.now());
                User user = findUserById(transaction.getUserId());
                user.decrementBorrowedBooks();
                System.out.println("Book \"" + book.getTitle() + "\" has been returned.");
                calculateFine(transaction); // Calculate and display fine, if applicable
            }
        } else {
            System.out.println("Book not found or is already available.");
        }
    }

    public List<Book> searchBooks(String searchTerm) {
        List<Book> searchResults = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    book.getCategory().toLowerCase().contains(searchTerm.toLowerCase())) {
                searchResults.add(book);
            }
        }
        return searchResults;
    }

    private void calculateFine(Transaction transaction) {
        LocalDate dueDate = transaction.getBorrowDate().plusDays(15);
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isAfter(dueDate)) {
            long daysOverdue = currentDate.toEpochDay() - dueDate.toEpochDay();
            int fine = (int) (daysOverdue * 10); // Fine: 10 rupees per day
            System.out.println("Fine: " + fine + " rupees.");
        }
    }
}

class Empire {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        int studentId = -1; // Initialize studentId to a default value

        Book book1 = new Book(1, "Book 1", "Author A", "Fiction");
        Book book2 = new Book(2, "Book 2", "Author B", "Mystery");
        library.addBook(book1);
        library.addBook(book2);

        System.out.println("Welcome to the Library Management System!");
        int choice;
        do {
            System.out.println("1. Student Registration");
            System.out.println("2. Book Borrow");
            System.out.println("3. Book Return");
            System.out.println("4. Book Search");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Student ID: ");
                    studentId = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer
                    System.out.print("Enter Student Name: ");
                    String studentName = scanner.nextLine();
                    User student = new User(studentId, studentName);
                    library.registerStudent(student);
                    System.out.println("Student \"" + student.getName() + "\" has been registered.");
                    break;
                case 2:
                    if (studentId == -1) {
                        System.out.println("Please register as a student first.");
                    } else {
                        System.out.print("Enter Book ID to borrow: ");
                        int borrowBookId = scanner.nextInt();
                        library.borrowBook(library.findBookById(borrowBookId), studentId);
                    }
                    break;
                case 3:
                    System.out.print("Enter Book ID to return: ");
                    int returnBookId = scanner.nextInt();
                    library.returnBook(returnBookId);
                    break;
                case 4:
                    scanner.nextLine(); // Clear the buffer
                    System.out.print("Enter search term (title, author, or category): ");
                    String searchTerm = scanner.nextLine();
                    List<Book> searchResults = library.searchBooks(searchTerm);
                    if (searchResults.isEmpty()) {
                        System.out.println("No books found matching the search term.");
                    } else {
                        System.out.println("Search Results:");
                        for (Book book : searchResults) {
                            System.out.println("Book ID: " + book.getBookId() + ", Title: " + book.getTitle() +
                                    ", Author: " + book.getAuthor() + ", Category: " + book.getCategory() +
                                    ", Availability: " + (book.isAvailable() ? "Available" : "Borrowed"));
                        }
                    }
                    break;
                case 5:
                    System.out.println("Exiting the Library Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 5);

        scanner.close();
    }
}