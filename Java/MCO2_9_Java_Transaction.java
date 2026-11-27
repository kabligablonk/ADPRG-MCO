/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "Transaction" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

import java.time.LocalDate;
import java.time.LocalTime;

// One cleaned row of bank_transactions.csv (financial fields as numbers, date and time as date types)
public class MCO2_9_Java_Transaction {

    // Constructors
    public MCO2_9_Java_Transaction(String accountName, long accountNumber, LocalDate date, LocalTime time,
                                   String currency, double amount, String type, double currentBalance){

        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.date = date;
        this.time = time;
        this.currency = currency;
        this.amount = amount;
        this.type = type;
        this.currentBalance = currentBalance;
    }

    // Methods
    public String getAccountName(){
        return accountName;
    }

    public long getAccountNumber(){
        return accountNumber;
    }

    public LocalDate getDate(){
        return date;
    }

    public LocalTime getTime(){
        return time;
    }

    public String getCurrency(){
        return currency;
    }

    public double getAmount(){
        return amount;
    }

    public String getType(){
        return type;
    }

    public double getCurrentBalance(){
        return currentBalance;
    }

    // Transaction Attributes
    private String accountName;
    private long accountNumber;
    private LocalDate date;
    private LocalTime time;
    private String currency;
    private double amount;
    private String type; // "deposit" or "withdrawal"
    private double currentBalance;

}