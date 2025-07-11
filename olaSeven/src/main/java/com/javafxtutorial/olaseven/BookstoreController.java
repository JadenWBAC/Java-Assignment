package com.javafxtutorial.olaseven;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class BookstoreController implements Initializable {

    // FXML components - Book Management
    @FXML private TextField bookIsbnField, bookTitleField, bookAuthorField, bookGenreField, bookPriceField, bookQuantityField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private ListView<String> bookListView;

    // FXML components - Customer Management
    @FXML private TextField customerIdField, customerFirstNameField, customerLastNameField, customerEmailField;
    @FXML private ListView<String> customerListView;

    // FXML components - Sales Management
    @FXML private ComboBox<String> saleBookCombo, saleCustomerCombo;
    @FXML private TextField saleQuantityField;
    @FXML private TextArea saleReceiptArea;
    @FXML private ListView<String> purchaseHistoryList;

    // FXML components - Revenue
    @FXML private Label totalRevenueLabel;

    // Data containers
    private final List<Book> books = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private double totalRevenue = 0.0;

    // Constants
    private static final String BOOKS_FILE = "Books";
    private static final String CUSTOMERS_FILE = "Customers";
    private static final String REVENUE_FILE = "revenue.txt";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComponents();
        loadData();
        updateAllViews();
    }

    private void initializeComponents() {
        searchTypeCombo.setItems(FXCollections.observableArrayList("Title", "Author", "ISBN", "Genre"));
        searchTypeCombo.setValue("Title");
    }

    private void loadData() {
        loadBooksFromFile();
        loadCustomersFromFile();
        loadRevenueFromFile();
    }

    private void updateAllViews() {
        updateBookListView();
        updateCustomerListView();
        updateSaleComboBoxes();
        updateTotalRevenue();
    }

    // Book Management Methods
    @FXML
    private void addBook() {
        try {
            String isbn = getFieldText(bookIsbnField);
            String title = getFieldText(bookTitleField);
            String author = getFieldText(bookAuthorField);
            String genre = getFieldText(bookGenreField);
            double price = parseDouble(bookPriceField.getText());
            int quantity = parseInt(bookQuantityField.getText());

            if (!validateBookInput(isbn, title, author, genre)) return;

            // Check if book already exists and update quantity
            Book existingBook = findBookByIsbn(isbn);
            if (existingBook != null) {
                existingBook.setQuantity(existingBook.getQuantity() + quantity);
                showAlert("Info", "Book already exists. Quantity updated!");
            } else {
                books.add(new Book(isbn, title, author, genre, price, quantity));
                showAlert("Success", "Book added successfully!");
            }

            clearBookFields();
            updateBookRelatedViews();
            saveBooksToFile();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for price and quantity!");
        }
    }

    @FXML
    private void searchBooks() {
        String searchTerm = getFieldText(searchField).toLowerCase();

        if (searchTerm.isEmpty()) {
            updateBookListView();
            return;
        }

        Predicate<Book> searchPredicate = createSearchPredicate(searchTerm, searchTypeCombo.getValue());
        ObservableList<String> searchResults = books.stream()
                .filter(searchPredicate)
                .map(Book::toDisplayString)
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);

        bookListView.setItems(searchResults);
    }

    // Customer Management Methods
    @FXML
    private void addCustomer() {
        String customerId = getFieldText(customerIdField);
        String firstName = getFieldText(customerFirstNameField);
        String lastName = getFieldText(customerLastNameField);
        String email = getFieldText(customerEmailField);

        if (!validateCustomerInput(customerId, firstName, lastName, email)) return;

        if (findCustomerById(customerId) != null) {
            showAlert("Error", "Customer ID already exists!");
            return;
        }

        customers.add(new Customer(customerId, firstName, lastName, email));
        clearCustomerFields();
        updateCustomerRelatedViews();
        saveCustomersToFile();
        showAlert("Success", "Customer added successfully!");
    }

    // Sales Management Methods
    @FXML
    private void processSale() {
        try {
            String selectedBook = saleBookCombo.getValue();
            String selectedCustomer = saleCustomerCombo.getValue();
            int quantity = parseInt(saleQuantityField.getText());

            if (!validateSaleInput(selectedBook, selectedCustomer)) return;

            Book book = findBookByDisplayString(selectedBook);
            Customer customer = findCustomerByDisplayString(selectedCustomer);

            if (book == null || customer == null) {
                showAlert("Error", "Selected book or customer not found!");
                return;
            }

            if (book.getQuantity() < quantity) {
                showAlert("Error", "Insufficient stock! Available: " + book.getQuantity());
                return;
            }

            processSaleTransaction(book, customer, quantity);
            showAlert("Success", "Sale processed successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid quantity!");
        }
    }

    private void processSaleTransaction(Book book, Customer customer, int quantity) {
        double saleAmount = book.getPrice() * quantity;

        // Update inventory and revenue
        book.setQuantity(book.getQuantity() - quantity);
        totalRevenue += saleAmount;

        // Add to customer purchase history
        String purchase = String.format("%s - Qty: %d - $%.2f", book.getTitle(), quantity, saleAmount);
        customer.addPurchase(purchase);

        // Generate and display receipt
        generateSaleReceipt(book, customer, quantity, saleAmount);

        // Update views and save data
        updateAllViews();
        saveAllData();
        saleQuantityField.clear();
    }

    private void generateSaleReceipt(Book book, Customer customer, int quantity, double saleAmount) {
        String receipt = String.format(
                "SALE RECEIPT\n" +
                        "Customer: %s %s\n" +
                        "Book: %s\n" +
                        "Author: %s\n" +
                        "Quantity: %d\n" +
                        "Unit Price: $%.2f\n" +
                        "Total Amount: $%.2f\n" +
                        "Remaining Stock: %d",
                customer.getFirstName(), customer.getLastName(),
                book.getTitle(), book.getAuthor(),
                quantity, book.getPrice(), saleAmount,
                book.getQuantity()
        );
        saleReceiptArea.setText(receipt);
    }

    @FXML
    private void viewPurchaseHistory() {
        String selectedCustomer = saleCustomerCombo.getValue();
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer!");
            return;
        }

        Customer customer = findCustomerByDisplayString(selectedCustomer);
        if (customer != null) {
            purchaseHistoryList.setItems(FXCollections.observableArrayList(customer.getPurchaseHistory()));
        }
    }

    // Helper Methods - Input Validation
    private boolean validateBookInput(String isbn, String title, String author, String genre) {
        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return false;
        }
        return true;
    }

    private boolean validateCustomerInput(String customerId, String firstName, String lastName, String email) {
        if (customerId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert("Error", "All customer fields must be filled!");
            return false;
        }
        return true;
    }

    private boolean validateSaleInput(String selectedBook, String selectedCustomer) {
        if (selectedBook == null || selectedCustomer == null) {
            showAlert("Error", "Please select both book and customer!");
            return false;
        }
        return true;
    }

    // Helper Methods - Data Operations
    private String getFieldText(TextField field) {
        return field.getText().trim();
    }

    private double parseDouble(String text) throws NumberFormatException {
        return Double.parseDouble(text.trim());
    }

    private int parseInt(String text) throws NumberFormatException {
        return Integer.parseInt(text.trim());
    }

    private Book findBookByIsbn(String isbn) {
        return books.stream().filter(book -> book.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    private Customer findCustomerById(String customerId) {
        return customers.stream().filter(customer -> customer.getCustomerId().equals(customerId)).findFirst().orElse(null);
    }

    private Book findBookByDisplayString(String displayString) {
        return books.stream().filter(book -> book.toDisplayString().equals(displayString)).findFirst().orElse(null);
    }

    private Customer findCustomerByDisplayString(String displayString) {
        return customers.stream().filter(customer -> customer.toDisplayString().equals(displayString)).findFirst().orElse(null);
    }

    private Predicate<Book> createSearchPredicate(String searchTerm, String searchType) {
        switch (searchType) {
            case "Title": return book -> book.getTitle().toLowerCase().contains(searchTerm);
            case "Author": return book -> book.getAuthor().toLowerCase().contains(searchTerm);
            case "ISBN": return book -> book.getIsbn().toLowerCase().contains(searchTerm);
            case "Genre": return book -> book.getGenre().toLowerCase().contains(searchTerm);
            default: return book -> false;
        }
    }

    // Helper Methods - UI Updates
    private void clearBookFields() {
        bookIsbnField.clear();
        bookTitleField.clear();
        bookAuthorField.clear();
        bookGenreField.clear();
        bookPriceField.clear();
        bookQuantityField.clear();
    }

    private void clearCustomerFields() {
        customerIdField.clear();
        customerFirstNameField.clear();
        customerLastNameField.clear();
        customerEmailField.clear();
    }

    private void updateBookListView() {
        ObservableList<String> bookStrings = books.stream()
                .map(Book::toDisplayString)
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
        bookListView.setItems(bookStrings);
    }

    private void updateCustomerListView() {
        ObservableList<String> customerStrings = customers.stream()
                .map(Customer::toDisplayString)
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
        customerListView.setItems(customerStrings);
    }

    private void updateSaleComboBoxes() {
        // Update book combo with books that have stock
        ObservableList<String> bookOptions = books.stream()
                .filter(book -> book.getQuantity() > 0)
                .map(Book::toDisplayString)
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
        saleBookCombo.setItems(bookOptions);

        // Update customer combo
        ObservableList<String> customerOptions = customers.stream()
                .map(Customer::toDisplayString)
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
        saleCustomerCombo.setItems(customerOptions);
    }

    private void updateTotalRevenue() {
        totalRevenueLabel.setText(String.format("Total Revenue: $%.2f", totalRevenue));
    }

    private void updateBookRelatedViews() {
        updateBookListView();
        updateSaleComboBoxes();
    }

    private void updateCustomerRelatedViews() {
        updateCustomerListView();
        updateSaleComboBoxes();
    }

    // File I/O Methods
    private void loadBooksFromFile() {
        try {
            // Try to load from resources first
            InputStream inputStream = getClass().getResourceAsStream("/" + BOOKS_FILE);
            if (inputStream != null) {
                loadBooksFromStream(inputStream);
            } else {
                // Fall back to file system
                try (BufferedReader br = new BufferedReader(new FileReader(BOOKS_FILE))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        Book book = parseBookFromLine(line);
                        if (book != null) books.add(book);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading books file: " + e.getMessage());
        }
    }

    private void loadBooksFromStream(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                Book book = parseBookFromLine(line);
                if (book != null) books.add(book);
            }
        }
    }

    private Book parseBookFromLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 6) {
                return new Book(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        Double.parseDouble(parts[4].trim()),
                        Integer.parseInt(parts[5].trim())
                );
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing book line: " + line);
        }
        return null;
    }

    private void saveBooksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            books.forEach(book -> pw.println(book.toString()));
        } catch (IOException e) {
            System.err.println("Error saving books file: " + e.getMessage());
        }
    }

    private void loadCustomersFromFile() {
        try {
            // Try to load from resources first
            InputStream inputStream = getClass().getResourceAsStream("/" + CUSTOMERS_FILE);
            if (inputStream != null) {
                loadCustomersFromStream(inputStream);
            } else {
                // Fall back to file system
                try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        Customer customer = Customer.fromString(line);
                        if (customer != null) customers.add(customer);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading Customers file: " + e.getMessage());
        }
    }

    private void loadCustomersFromStream(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                Customer customer = Customer.fromString(line);
                if (customer != null) customers.add(customer);
            }
        }
    }

    private void saveCustomersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            customers.forEach(customer -> pw.println(customer.toString()));
        } catch (IOException e) {
            System.err.println("Error saving customers file: " + e.getMessage());
        }
    }

    private void loadRevenueFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(REVENUE_FILE))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty()) {
                totalRevenue = Double.parseDouble(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            totalRevenue = 0.0; // Start with 0 if file doesn't exist or is invalid
        }
    }

    private void saveRevenueToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(REVENUE_FILE))) {
            pw.println(totalRevenue);
        } catch (IOException e) {
            System.err.println("Error saving revenue file: " + e.getMessage());
        }
    }

    private void saveAllData() {
        saveBooksToFile();
        saveCustomersToFile();
        saveRevenueToFile();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}