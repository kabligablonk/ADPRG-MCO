/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "TierRecord" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

// One row of Report 1: Banking Privilege Tiers
public class MCO2_9_Java_TierRecord {

    // Constructors
    public MCO2_9_Java_TierRecord(String accountName, long accountAge, double currentBalance,
                                  double averageDailyBalance, String privilegeTier){

        this.accountName = accountName;
        this.accountAge = accountAge;
        this.currentBalance = currentBalance;
        this.averageDailyBalance = averageDailyBalance;
        this.privilegeTier = privilegeTier;
    }

    // Methods
    public String getAccountName(){
        return accountName;
    }

    public long getAccountAge(){
        return accountAge;
    }

    public double getCurrentBalance(){
        return currentBalance;
    }

    public double getAverageDailyBalance(){
        return averageDailyBalance;
    }

    public String getPrivilegeTier(){
        return privilegeTier;
    }

    // TierRecord Attributes
    private String accountName;
    private long accountAge; // Days since the account was opened
    private double currentBalance;
    private double averageDailyBalance;
    private String privilegeTier;

}