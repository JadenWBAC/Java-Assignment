package com.javafxtutorial.olaseven;

import java.util.Objects;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private double price;
    private int quantity;

    // Default constructor
    public Book() {
    }

    // Constructor with all parameters
    public Book(String isbn, String title, String author, String genre, double price, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = Math.max(0, price); // Ensure non-negative price
        this.quantity = Math.max(0, quantity); // Ensure non-negative quantity
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    // Setters with validation
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }

    public void setPrice(double price) {
        this.price = Math.max(0, price);
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    // Utility methods
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public boolean removeQuantity(int amount) {
        if (amount > 0 && amount <= this.quantity) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    public double getTotalValue() {
        return price * quantity;
    }

    // String representations
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%.2f,%d", isbn, title, author, genre, price, quantity);
    }

    public String toDisplayString() {
        return String.format("ISBN: %s | Title: %s | Author: %s | Genre: %s | Price: $%.2f | Quantity: %d",
                isbn, title, author, genre, price, quantity);
    }

    public String toShortDisplayString() {
        return String.format("%s by %s ($%.2f)", title, author, price);
    }

    // Object equality and hash
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}