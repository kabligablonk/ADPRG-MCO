/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "DailyRecord" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

import java.time.LocalDate;

// One row of Report 3: Suspicious Client Behavior (one account on one day)
public class MCO2_9_Java_DailyRecord {

    // Constructors
    public MCO2_9_Java_DailyRecord(String accountName, long accountNumber, LocalDate date){

        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.date = date;

        totalWithdrawalAmounts = 0;
        numberOfDeposits = 0;
        totalDepositAmounts = 0;
        turnoverVelocity = 0;
        velocityDefined = false;
        riskFlagLevel = "Normal";
    }

    // Methods
    public void addDeposit(double amount){
        numberOfDeposits++;
        totalDepositAmounts = totalDepositAmounts + amount;
    }

    public void addWithdrawal(double amount){
        totalWithdrawalAmounts = totalWithdrawalAmounts + amount;
    }

    // Call once after all the day's transactions were added
    public void calculateRisk(){
        totalWithdrawalAmounts = Math.round(totalWithdrawalAmounts * 100) / 100.0;
        totalDepositAmounts = Math.round(totalDepositAmounts * 100) / 100.0;

        // TurnoverVelocity (%) = TotalWithdrawal / TotalDeposit x 100
        // With no deposits that day the division is undefined, so there is no velocity (shown as N/A)
        // and only the withdrawal amount rule can flag the day.
        velocityDefined = totalDepositAmounts > 0;
        turnoverVelocity = 0;

        if(velocityDefined){
            turnoverVelocity = Math.round(totalWithdrawalAmounts / totalDepositAmounts * 100 * 100) / 100.0; // 2 decimals
        }

        // Suspicious if withdrawals are at least 500,000.00 OR the turnover velocity is at least 90%
        if(totalWithdrawalAmounts >= 500000 || (velocityDefined && turnoverVelocity >= 90)){
            riskFlagLevel = "Suspicious";
        }
        else{
            riskFlagLevel = "Normal";
        }
    }

    public String getAccountName(){
        return accountName;
    }

    public long getAccountNumber(){
        return accountNumber;
    }

    public LocalDate getDate(){
        return date;
    }

    public double getTotalWithdrawalAmounts(){
        return totalWithdrawalAmounts;
    }

    public int getNumberOfDeposits(){
        return numberOfDeposits;
    }

    public double getTotalDepositAmounts(){
        return totalDepositAmounts;
    }

    public double getTurnoverVelocity(){
        return turnoverVelocity;
    }

    public boolean isVelocityDefined(){
        return velocityDefined;
    }

    public String getRiskFlagLevel(){
        return riskFlagLevel;
    }

    // DailyRecord Attributes
    private String accountName;
    private long accountNumber;
    private LocalDate date;
    private double totalWithdrawalAmounts;
    private int numberOfDeposits;
    private double totalDepositAmounts;
    private double turnoverVelocity;
    private boolean velocityDefined; // false when there were no deposits that day
    private String riskFlagLevel; // "Suspicious" or "Normal"

}