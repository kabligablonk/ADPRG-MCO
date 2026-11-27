/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "BankAccount" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

public class MCO2_9_Java_BankAccount {

    // Constructors
    public MCO2_9_Java_BankAccount(String accountName, long accountNumber, double balance){

        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Methods
    public String getAccountName(){
        return accountName;
    }

    public long getAccountNumber(){
        return accountNumber;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }

    // BankAccount Attributes
    private String accountName;
    private long accountNumber; // 10 digits, so it needs long
    private double balance;

}