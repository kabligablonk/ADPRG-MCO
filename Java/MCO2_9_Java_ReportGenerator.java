/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "ReportGenerator" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/*
    Generates the reports from the cleaned transactions (REQ-0006 onwards).

    Assumptions (for reports):
    - "Today" is the latest date found in the data, not the computer's clock, so the same file always
      gives the same reports.
    - "Last month" is the calendar month before the month of that latest date. (October)
    - The end-of-day balance of a day is the balance after the account's last transaction on or before that day.
      Days with no transaction keep the previous balance.
    - Report 2 projects 30 days of interest from each account's latest balance. The interest is added to the
      balance every day (rounded to 2 decimals), the same way as menu choice 6 (Show Interest Amount), so both
      screens give the same total for the same account.
    - Report 3 has one row for every account on every day that it had at least one transaction. If an account had no
      deposit on a day, its TurnoverVelocity is undefined (N/A) and only the withdrawal amount rule
      (at least 500,000.00) can mark that day as Suspicious.
    - Report 4 (own report) lists the 10 largest single transactions, deposits and withdrawals together.
    - summary.json uses all cleaned PHP transactions; its total interest expense is the total of Report 2.
*/

public class MCO2_9_Java_ReportGenerator {

    // Constructors
    public MCO2_9_Java_ReportGenerator(ArrayList<MCO2_9_Java_Transaction> transactions){

        // Sorted by date and time (the sort is stable, so rows with the same time stay in file order)
        this.transactions = new ArrayList<>(transactions);
        this.transactions.sort(Comparator.comparing(MCO2_9_Java_Transaction::getDate)
                .thenComparing(MCO2_9_Java_Transaction::getTime));

        referenceDate = LocalDate.now(); // Only used if there is no data
        if(!this.transactions.isEmpty()){
            referenceDate = this.transactions.get(this.transactions.size() - 1).getDate();
        }

        lastMonth = YearMonth.from(referenceDate).minusMonths(1);

        annualInterestRate = 0.05; // 5% per annum
        projectionDays = 30; // Report 2 projects interest after 30 days
    }

    // Methods
    // Report 1: Banking Privilege Tiers (REQ-0006) --------------------------------------------------------------
    public void generateReport1(){
        String fileName = "report1_banking_privilege_tiers.csv";
        ArrayList<MCO2_9_Java_TierRecord> rows = new ArrayList<>();
        ArrayList<Long> numbers = getAccountNumbers();
        ArrayList<MCO2_9_Java_Transaction> accTx;
        int i, day, index, daysInMonth = lastMonth.lengthOfMonth();
        double eodBalance, sumEOD, averageDailyBalance;
        long accountAge;
        LocalDate date;

        for(i = 0; i < numbers.size(); i++){
            accTx = getTransactionsOf(numbers.get(i));

            // Sum of the end-of-day balances for every day of last month
            sumEOD = 0;
            eodBalance = 0; // An account has no balance before its first transaction
            index = 0;

            for(day = 1; day <= daysInMonth; day++){
                date = lastMonth.atDay(day);

                while(index < accTx.size() && !accTx.get(index).getDate().isAfter(date)){
                    eodBalance = accTx.get(index).getCurrentBalance();
                    index++;
                }

                sumEOD = sumEOD + eodBalance;
            }

            // Rounded first, so the filter and tier use the same value that is displayed
            averageDailyBalance = Math.round(sumEOD / daysInMonth * 100) / 100.0;

            if(averageDailyBalance >= 100000){
                accountAge = ChronoUnit.DAYS.between(accTx.get(0).getDate(), referenceDate); // Days since first transaction

                rows.add(new MCO2_9_Java_TierRecord(accTx.get(accTx.size() - 1).getAccountName(), accountAge,
                        accTx.get(accTx.size() - 1).getCurrentBalance(), averageDailyBalance, getTier(averageDailyBalance)));
            }
        }

        // Descending by AverageDailyBalance (account name breaks ties so the output is always the same)
        rows.sort(Comparator.comparingDouble(MCO2_9_Java_TierRecord::getAverageDailyBalance).reversed()
                .thenComparing(MCO2_9_Java_TierRecord::getAccountName));

        // Console preview
        System.out.println("\nReport 1: Banking Privilege Tiers");
        System.out.println("(Based on last month: " + lastMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + lastMonth.getYear() + ", "
                + daysInMonth + " days. Age counted up to " + referenceDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
        System.out.printf("%-22s | %-10s | %-16s | %-20s | %-9s%n", "AccountName", "AccountAge", "CurrentBalance",
                "AverageDailyBalance", "PrivilegeTier");

        if(rows.isEmpty()){
            System.out.println("No clients met the filter (AverageDailyBalance of at least 100,000.00).");
        }

        for(i = 0; i < rows.size() && i < 2; i++){ // First 2 rows only
            System.out.printf("%-22s | %-10d | %-16s | %-20s | %-9s%n", rows.get(i).getAccountName(),
                    rows.get(i).getAccountAge(), String.format(Locale.US, "%,.2f", rows.get(i).getCurrentBalance()),
                    String.format(Locale.US, "%,.2f", rows.get(i).getAverageDailyBalance()), rows.get(i).getPrivilegeTier());
        }

        // Full table to CSV
        try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))){
            pw.print("AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier\n");

            for(i = 0; i < rows.size(); i++){
                pw.print(csvText(rows.get(i).getAccountName()) + "," + rows.get(i).getAccountAge() + ","
                        + csvMoney(rows.get(i).getCurrentBalance()) + "," + csvMoney(rows.get(i).getAverageDailyBalance()) + ","
                        + rows.get(i).getPrivilegeTier() + "\n");
            }

            System.out.println("\n(Full table exported to " + fileName + ")");
        }
        catch(IOException e){
            System.out.println("\nError: could not write " + fileName + ".");
        }
    }

    // Report 2: Interest Expense Projection (REQ-0007) ----------------------------------------------------------
    public void generateReport2(){
        String fileName = "report2_interest_expense_projection.csv";
        ArrayList<MCO2_9_Java_InterestRecord> rows = getInterestRows();
        int i;

        // Descending by ExpectedInterest (account name breaks ties so the output is always the same)
        rows.sort(Comparator.comparingDouble(MCO2_9_Java_InterestRecord::getExpectedInterest).reversed()
                .thenComparing(MCO2_9_Java_InterestRecord::getAccountName));

        // Console preview
        System.out.println("\nReport 2: Interest Expense Projection");
        System.out.println("(Based on the latest balances as of " + referenceDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + ", projected " + projectionDays + " days ahead)");
        System.out.printf("%-22s | %-16s | %-16s%n", "AccountName", "CurrentBalance", "ExpectedInterest");

        if(rows.isEmpty()){
            System.out.println("No clients met the filter (CurrentBalance of at least 10,000.00).");
        }

        for(i = 0; i < rows.size() && i < 2; i++){ // First 2 rows only
            System.out.printf("%-22s | %-16s | %-16s%n", rows.get(i).getAccountName(),
                    String.format(Locale.US, "%,.2f", rows.get(i).getCurrentBalance()),
                    String.format(Locale.US, "%,.2f", rows.get(i).getExpectedInterest()));
        }

        // Full table to CSV
        try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))){
            pw.print("AccountName,CurrentBalance,ExpectedInterest\n");

            for(i = 0; i < rows.size(); i++){
                pw.print(csvText(rows.get(i).getAccountName()) + "," + csvMoney(rows.get(i).getCurrentBalance()) + ","
                        + csvMoney(rows.get(i).getExpectedInterest()) + "\n");
            }

            System.out.println("\n(Full table exported to " + fileName + ")");
        }
        catch(IOException e){
            System.out.println("\nError: could not write " + fileName + ".");
        }
    }

    // Report 3: Suspicious Client Behavior (REQ-0008) -----------------------------------------------------------
    public void generateReport3(){
        String fileName = "report3_suspicious_client_behavior.csv";
        ArrayList<MCO2_9_Java_DailyRecord> rows = new ArrayList<>();
        MCO2_9_Java_DailyRecord found;
        MCO2_9_Java_Transaction tx;
        int i, j, suspiciousCount = 0;

        // Group the transactions by account and date. They are sorted by date, so only the rows
        // already created for the same date need to be searched.
        for(i = 0; i < transactions.size(); i++){
            tx = transactions.get(i);
            found = null;

            for(j = rows.size() - 1; j >= 0 && rows.get(j).getDate().equals(tx.getDate()); j--){
                if(rows.get(j).getAccountNumber() == tx.getAccountNumber()){
                    found = rows.get(j);
                }
            }

            if(found == null){
                found = new MCO2_9_Java_DailyRecord(tx.getAccountName(), tx.getAccountNumber(), tx.getDate());
                rows.add(found);
            }

            if(tx.getType().equals("deposit")){
                found.addDeposit(tx.getAmount());
            }
            else{
                found.addWithdrawal(tx.getAmount());
            }
        }

        for(i = 0; i < rows.size(); i++){
            rows.get(i).calculateRisk();

            if(rows.get(i).getRiskFlagLevel().equals("Suspicious")){
                suspiciousCount++;
            }
        }

        // Ascending by Date, then descending by RiskFlagLevel (Suspicious comes before Normal),
        // then by account name so the output is always the same
        rows.sort(Comparator.comparing(MCO2_9_Java_DailyRecord::getDate)
                .thenComparing(MCO2_9_Java_DailyRecord::getRiskFlagLevel, Comparator.reverseOrder())
                .thenComparing(MCO2_9_Java_DailyRecord::getAccountName));

        // Console preview
        System.out.println("\nReport 3: Suspicious Client Behavior");
        System.out.println("(" + String.format(Locale.US, "%,d", rows.size()) + " account-days monitored, "
                + String.format(Locale.US, "%,d", suspiciousCount) + " flagged Suspicious)");
        System.out.printf("%-20s | %-10s | %-23s | %-16s | %-19s | %-19s | %-13s%n", "AccountName", "Date",
                "TotalWithdrawalAmounts", "NumberOfDeposits", "TotalDepositAmounts", "TurnoverVelocity(%)", "RiskFlagLevel");

        if(rows.isEmpty()){
            System.out.println("No transactions to monitor.");
        }

        for(i = 0; i < rows.size() && i < 2; i++){ // First 2 rows only
            System.out.printf("%-20s | %-10s | %-23s | %-16d | %-19s | %-19s | %-13s%n", rows.get(i).getAccountName(),
                    rows.get(i).getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    String.format(Locale.US, "%,.2f", rows.get(i).getTotalWithdrawalAmounts()), rows.get(i).getNumberOfDeposits(),
                    String.format(Locale.US, "%,.2f", rows.get(i).getTotalDepositAmounts()),
                    velocityText(rows.get(i), false), rows.get(i).getRiskFlagLevel());
        }

        // Full table to CSV
        try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))){
            pw.print("AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,TotalDepositAmounts,TurnoverVelocity(%),RiskFlagLevel\n");

            for(i = 0; i < rows.size(); i++){
                pw.print(csvText(rows.get(i).getAccountName()) + ","
                        + rows.get(i).getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ","
                        + csvMoney(rows.get(i).getTotalWithdrawalAmounts()) + "," + rows.get(i).getNumberOfDeposits() + ","
                        + csvMoney(rows.get(i).getTotalDepositAmounts()) + "," + velocityText(rows.get(i), true) + ","
                        + rows.get(i).getRiskFlagLevel() + "\n");
            }

            System.out.println("\n(Full table exported to " + fileName + ")");
        }
        catch(IOException e){
            System.out.println("\nError: could not write " + fileName + ".");
        }
    }

    // TurnoverVelocity as text: N/A when there were no deposits, otherwise comma-formatted with 2 decimals
    private String velocityText(MCO2_9_Java_DailyRecord row, boolean forCsv){
        if(!row.isVelocityDefined()){
            return "N/A";
        }

        if(forCsv){
            return csvMoney(row.getTurnoverVelocity());
        }

        return String.format(Locale.US, "%,.2f", row.getTurnoverVelocity());
    }

    // Report 4: Top 10 Largest Transactions - own report (REQ-0009) ------------------------------------------
    /*
        Reason for report 4: Banks usually review their largest transactions because big deposits and withdrawals
        are unusual and may seem suspicious (could be a sign of fraud or corruption). This report allows banks to
        immediately see their biggest transactions in order to easily review them.
    */
    public void generateReport4(){
        String fileName = "report4_top_10_largest_transactions.csv";
        ArrayList<MCO2_9_Java_Transaction> sorted = new ArrayList<>(transactions);
        int i, count;
        String type;

        // Descending by Amount (date, time and account name break ties so the output is always the same)
        sorted.sort(Comparator.comparingDouble(MCO2_9_Java_Transaction::getAmount).reversed()
                .thenComparing(MCO2_9_Java_Transaction::getDate)
                .thenComparing(MCO2_9_Java_Transaction::getTime)
                .thenComparing(MCO2_9_Java_Transaction::getAccountName));

        count = Math.min(10, sorted.size());

        // Console preview
        System.out.println("\nReport 4: Top 10 Largest Transactions");
        System.out.printf("%-22s | %-10s | %-15s | %-16s%n", "AccountName", "Date", "TransactionType", "Amount");

        if(count == 0){
            System.out.println("No transactions found.");
        }

        for(i = 0; i < count && i < 2; i++){ // First 2 rows only
            System.out.printf("%-22s | %-10s | %-15s | %-16s%n", sorted.get(i).getAccountName(),
                    sorted.get(i).getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    capitalize(sorted.get(i).getType()), String.format(Locale.US, "%,.2f", sorted.get(i).getAmount()));
        }

        // Full table to CSV
        try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))){
            pw.print("AccountName,Date,TransactionType,Amount\n");

            for(i = 0; i < count; i++){
                pw.print(csvText(sorted.get(i).getAccountName()) + ","
                        + sorted.get(i).getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ","
                        + capitalize(sorted.get(i).getType()) + "," + csvMoney(sorted.get(i).getAmount()) + "\n");
            }

            System.out.println("\n(Full table exported to " + fileName + ")");
        }
        catch(IOException e){
            System.out.println("\nError: could not write " + fileName + ".");
        }
    }

    private String capitalize(String text){
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    // Summary: summary.json (REQ-0010) -------------------------------------------------------------------------
    /*
        Totals are over all the cleaned PHP transactions in the file. total_balance is the sum of every account's
        latest balance, and total_interest_expense is the sum of the ExpectedInterest column of Report 2.
        Amounts are written as plain numbers with 2 decimals because JSON numbers cannot contain commas.
    */
    public void generateSummary(){
        String fileName = "summary.json";
        ArrayList<Long> numbers = getAccountNumbers();
        ArrayList<MCO2_9_Java_InterestRecord> interestRows = getInterestRows();
        ArrayList<MCO2_9_Java_Transaction> accTx;
        int i;
        double totalDeposits = 0, totalWithdrawals = 0, totalBalance = 0, totalInterestExpense = 0;
        String json;

        for(i = 0; i < transactions.size(); i++){
            if(transactions.get(i).getType().equals("deposit")){
                totalDeposits = totalDeposits + transactions.get(i).getAmount();
            }
            else{
                totalWithdrawals = totalWithdrawals + transactions.get(i).getAmount();
            }
        }

        for(i = 0; i < numbers.size(); i++){
            accTx = getTransactionsOf(numbers.get(i));
            totalBalance = totalBalance + accTx.get(accTx.size() - 1).getCurrentBalance(); // Latest balance
        }

        for(i = 0; i < interestRows.size(); i++){
            totalInterestExpense = totalInterestExpense + interestRows.get(i).getExpectedInterest();
        }

        json = "{\n"
                + "  \"total_accounts\": " + numbers.size() + ",\n"
                + "  \"total_deposits\": " + String.format(Locale.US, "%.2f", totalDeposits) + ",\n"
                + "  \"total_withdrawals\": " + String.format(Locale.US, "%.2f", totalWithdrawals) + ",\n"
                + "  \"total_balance\": " + String.format(Locale.US, "%.2f", totalBalance) + ",\n"
                + "  \"total_interest_expense\": " + String.format(Locale.US, "%.2f", totalInterestExpense) + "\n"
                + "}";

        System.out.println("\nSummary State (" + fileName + "):");
        System.out.println(json);

        try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))){
            pw.print(json + "\n");
        }
        catch(IOException e){
            System.out.println("\nError: could not write " + fileName + ".");
        }
    }

    // The rows of Report 2 (shared by Report 2 and the summary): every account with a current balance of at least 10,000.00
    private ArrayList<MCO2_9_Java_InterestRecord> getInterestRows(){
        ArrayList<MCO2_9_Java_InterestRecord> rows = new ArrayList<>();
        ArrayList<Long> numbers = getAccountNumbers();
        ArrayList<MCO2_9_Java_Transaction> accTx;
        int i, day;
        double currentBalance, projectedBalance, dailyInterest, expectedInterest;

        for(i = 0; i < numbers.size(); i++){
            accTx = getTransactionsOf(numbers.get(i));
            currentBalance = accTx.get(accTx.size() - 1).getCurrentBalance(); // Latest balance

            if(currentBalance >= 10000){
                // Daily Interest = End-of-Day Balance x (Annual Interest Rate / 365), added to the balance each day
                projectedBalance = currentBalance;
                expectedInterest = 0;

                for(day = 1; day <= projectionDays; day++){
                    dailyInterest = Math.round(projectedBalance * (annualInterestRate / 365) * 100) / 100.0;
                    projectedBalance = Math.round((projectedBalance + dailyInterest) * 100) / 100.0;
                    expectedInterest = expectedInterest + dailyInterest;
                }

                expectedInterest = Math.round(expectedInterest * 100) / 100.0;

                rows.add(new MCO2_9_Java_InterestRecord(accTx.get(accTx.size() - 1).getAccountName(),
                        currentBalance, expectedInterest));
            }
        }

        return rows;
    }

    private String getTier(double averageDailyBalance){
        if(averageDailyBalance < 500000){
            return "Silver";
        }
        else if(averageDailyBalance < 1000000){
            return "Gold";
        }
        else{
            return "Platinum";
        }
    }

    // Helpers shared by the reports -------------------------------------------------------------------------------
    // Each account number once, in order of first appearance
    private ArrayList<Long> getAccountNumbers(){
        ArrayList<Long> numbers = new ArrayList<>();
        int i;

        for(i = 0; i < transactions.size(); i++){
            if(!numbers.contains(transactions.get(i).getAccountNumber())){
                numbers.add(transactions.get(i).getAccountNumber());
            }
        }

        return numbers;
    }

    // All transactions of one account, oldest first
    private ArrayList<MCO2_9_Java_Transaction> getTransactionsOf(long accountNumber){
        ArrayList<MCO2_9_Java_Transaction> accTx = new ArrayList<>();
        int i;

        for(i = 0; i < transactions.size(); i++){
            if(transactions.get(i).getAccountNumber() == accountNumber){
                accTx.add(transactions.get(i));
            }
        }

        return accTx;
    }

    // Comma-formatted number rounded to 2 decimals, in quotes so the comma stays inside one csv column (REQ-0012)
    private String csvMoney(double value){
        return "\"" + String.format(Locale.US, "%,.2f", value) + "\"";
    }

    private String csvText(String text){
        if(text.contains(",") || text.contains("\"")){
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }

        return text;
    }

    // ReportGenerator Attributes
    private ArrayList<MCO2_9_Java_Transaction> transactions;
    private LocalDate referenceDate; // Latest date in the data
    private YearMonth lastMonth;
    private double annualInterestRate;
    private int projectionDays;

}
