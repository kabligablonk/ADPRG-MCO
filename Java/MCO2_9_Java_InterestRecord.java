/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "InterestRecord" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

// One row of Report 2: Interest Expense Projection
public class MCO2_9_Java_InterestRecord {

    // Constructors
    public MCO2_9_Java_InterestRecord(String accountName, double currentBalance, double expectedInterest){

        this.accountName = accountName;
        this.currentBalance = currentBalance;
        this.expectedInterest = expectedInterest;
    }

    // Methods
    public String getAccountName(){
        return accountName;
    }

    public double getCurrentBalance(){
        return currentBalance;
    }

    public double getExpectedInterest(){
        return expectedInterest;
    }

    // InterestRecord Attributes
    private String accountName;
    private double currentBalance;
    private double expectedInterest;

}