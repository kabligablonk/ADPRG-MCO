/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "BankAccount" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO1_9_Java.java"

public class MCO1_9_Java_BankAccountClass {

    // Constructors
    public MCO1_9_Java_BankAccountClass(String accountName){

        this.accountName = accountName;
        balance = 1000;
    }

    // Methods
    public String getAccountName(){
        return accountName;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }

    // BankAccount Attributes
    private String accountName;
    private double balance;

}
