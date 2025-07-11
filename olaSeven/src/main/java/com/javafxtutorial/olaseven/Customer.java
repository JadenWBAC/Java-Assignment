package com.javafxtutorial.olaseven;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> purchaseHistory;

    public Customer() {
        this.purchaseHistory = new ArrayList<>();
    }

    public Customer(String customerId, String firstName, String lastName, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.purchaseHistory = new ArrayList<>();
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void setPurchaseHistory(List<String> purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public void addPurchase(String purchase) {
        this.purchaseHistory.add(purchase);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s,%s,%s,%s", customerId, firstName, lastName, email));

        // Add purchase history separated by semicolons
        if (!purchaseHistory.isEmpty()) {
            sb.append(",");
            for (int i = 0; i < purchaseHistory.size(); i++) {
                sb.append(purchaseHistory.get(i));
                if (i < purchaseHistory.size() - 1) {
                    sb.append(";");
                }
            }
        }
        return sb.toString();
    }

    public String toDisplayString() {
        return String.format("ID: %s | Name: %s %s | Email: %s",
                customerId, firstName, lastName, email);
    }

    // Parse customer from file string including purchase history
    public static Customer fromString(String line) {
        String[] parts = line.split(",", 5); // Limit split to 5 parts max
        if (parts.length >= 4) {
            Customer customer = new Customer(parts[0].trim(), parts[1].trim(),
                    parts[2].trim(), parts[3].trim());

            // Parse purchase history if it exists
            if (parts.length == 5 && !parts[4].trim().isEmpty()) {
                String[] purchases = parts[4].split(";");
                for (String purchase : purchases) {
                    customer.addPurchase(purchase.trim());
                }
            }
            return customer;
        }
        return null;
    }
}