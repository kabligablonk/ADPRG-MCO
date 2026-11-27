/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "DataLoader" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

/*
    Handles REQ-0003 to REQ-0005: reads the csv file, validates each row, cleans it, and keeps PHP rows only.

    Cleaning decisions (for the report):
    - Rows with invalid/wrong/malicious characteristics are FILTERED (removed and logged). It is better to filter rather
      than create made-up values because made-up values can make the reports wrong.
    - Formatting is considerate: extra spaces are trimmed, type and currency are converted to lower/upper case, and
      times with or without a leading zero (8:22:00 / 08:22:00) are accepted.
    - Valid rows with a currency other than PHP are excluded from the reports.
*/

public class MCO2_9_Java_DataLoader {

    // Constructors
    public MCO2_9_Java_DataLoader(String fileName){

        this.fileName = fileName;
        logFileName = "validation_log.txt";
        columnNames = new String[]{"account_name", "account_number", "date", "time", "currency", "amount", "type", "current_balance"};

        // STRICT so impossible dates such as 31/02/2026 are rejected instead of adjusted
        dateFormat = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
        timeFormat = DateTimeFormatter.ofPattern("H:mm:ss").withResolverStyle(ResolverStyle.STRICT);
    }

    // Methods
    public ArrayList<MCO2_9_Java_Transaction> load(){
        ArrayList<MCO2_9_Java_Transaction> transactions = new ArrayList<>();
        String line;
        MCO2_9_Java_Transaction row;
        int lineNumber = 1; // Line 1 is the header

        // Reset the counts so the loader can be run again
        totalRows = 0;
        invalidRows = 0;
        nonPhpRows = 0;
        errors = new ArrayList<>();
        fileFound = true;

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            br.readLine(); // Skip header

            while((line = br.readLine()) != null){
                lineNumber++;

                if(line.trim().isEmpty()){
                    continue; // Ignore blank lines
                }

                totalRows++;

                row = parseRow(line);

                if(row == null){
                    invalidRows++;
                    errors.add("Line " + lineNumber + ": " + rowError);
                }
                else if(!row.getCurrency().equals("PHP")){
                    nonPhpRows++; // Valid row, but not Philippine currency
                }
                else{
                    transactions.add(row);
                }
            }
        }
        catch(IOException e){
            fileFound = false;
        }

        if(fileFound){
            writeLog(transactions.size());
        }

        return transactions;
    }

    // Returns the cleaned row, or null if the row is invalid (the reason is stored in rowError)
    private MCO2_9_Java_Transaction parseRow(String line){
        String[] columns = line.split(",", -1); // -1 keeps empty trailing columns so missing values are detected
        int i;
        String type, currency;
        long accountNumber;
        double amount, balance;
        LocalDate date;
        LocalTime time;

        if(columns.length != columnNames.length){
            rowError = "expected " + columnNames.length + " columns but found " + columns.length;
            return null;
        }

        for(i = 0; i < columns.length; i++){
            columns[i] = columns[i].trim();

            if(columns[i].isEmpty()){
                rowError = "missing value for " + columnNames[i];
                return null;
            }
        }

        try{
            accountNumber = Long.parseLong(columns[1]);
        }
        catch(NumberFormatException e){
            rowError = "invalid account number '" + columns[1] + "'";
            return null;
        }

        if(columns[1].length() != 10 || accountNumber <= 0){
            rowError = "account number must be 10 digits '" + columns[1] + "'";
            return null;
        }

        try{
            date = LocalDate.parse(columns[2], dateFormat);
        }
        catch(DateTimeParseException e){
            rowError = "invalid date '" + columns[2] + "'";
            return null;
        }

        try{
            time = LocalTime.parse(columns[3], timeFormat);
        }
        catch(DateTimeParseException e){
            rowError = "invalid time '" + columns[3] + "'";
            return null;
        }

        currency = columns[4].toUpperCase();

        try{
            amount = Double.parseDouble(columns[5]);
        }
        catch(NumberFormatException e){
            rowError = "invalid amount '" + columns[5] + "'";
            return null;
        }

        if(Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0){
            rowError = "amount must be a positive number '" + columns[5] + "'";
            return null;
        }

        type = columns[6].toLowerCase();

        if(!type.equals("deposit") && !type.equals("withdrawal")){
            rowError = "unknown transaction type '" + columns[6] + "'";
            return null;
        }

        try{
            balance = Double.parseDouble(columns[7]);
        }
        catch(NumberFormatException e){
            rowError = "invalid balance '" + columns[7] + "'";
            return null;
        }

        if(Double.isNaN(balance) || Double.isInfinite(balance) || balance < 0){
            rowError = "balance must be a non-negative number '" + columns[7] + "'";
            return null;
        }

        return new MCO2_9_Java_Transaction(columns[0], accountNumber, date, time, currency, amount, type, balance);
    }

    private void writeLog(int keptRows){
        int i;

        try(PrintWriter pw = new PrintWriter(new FileWriter(logFileName))){
            pw.println("Data validation log for " + fileName);
            pw.println("Total rows read: " + totalRows);
            pw.println("Invalid rows removed: " + invalidRows);
            pw.println("Non-PHP rows excluded: " + nonPhpRows);
            pw.println("Rows kept (PHP): " + keptRows);

            if(!errors.isEmpty()){
                pw.println();
                pw.println("Errors found:");

                for(i = 0; i < errors.size(); i++){
                    pw.println(errors.get(i));
                }
            }
        }
        catch(IOException e){
            System.out.println("Could not write " + logFileName + ".");
        }
    }

    public boolean isFileFound(){
        return fileFound;
    }

    public int getTotalRows(){
        return totalRows;
    }

    public int getInvalidRows(){
        return invalidRows;
    }

    public int getNonPhpRows(){
        return nonPhpRows;
    }

    public String getLogFileName(){
        return logFileName;
    }

    // DataLoader Attributes
    private String fileName;
    private String logFileName;
    private String[] columnNames;
    private DateTimeFormatter dateFormat;
    private DateTimeFormatter timeFormat;
    private int totalRows;
    private int invalidRows;
    private int nonPhpRows;
    private boolean fileFound;
    private ArrayList<String> errors;
    private String rowError; // Reason the last row was rejected

}