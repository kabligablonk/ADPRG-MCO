/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "FileHandler" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO2_9_Java.java"

import java.io.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MCO2_9_Java_FileHandler {

    // Constructors
    public MCO2_9_Java_FileHandler(){

        fileName = "bank_transactions.csv";
        header = "account_name,account_number,date,time,currency,amount,type,current_balance";
        firstAccountNumber = 1092000001L; // Used when the file doesn't exist yet or has no accounts
    }

    // Methods
    // Reads the file once and builds one BankAccount per account number, using its latest balance
    public ArrayList<MCO2_9_Java_BankAccount> loadAccounts(){
        ArrayList<MCO2_9_Java_BankAccount> accounts = new ArrayList<>();
        String line;
        String[] columns;
        String name;
        long number;
        double balance;
        int i, found;

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            br.readLine(); // Skip header

            while((line = br.readLine()) != null){
                columns = line.split(",");

                if(columns.length >= 8){ // Skip malformed rows instead of crashing
                    try{
                        name = columns[0].trim();
                        number = Long.parseLong(columns[1].trim());
                        balance = Double.parseDouble(columns[7].trim());

                        found = -1;
                        for(i = 0; i < accounts.size(); i++){
                            if(accounts.get(i).getAccountNumber() == number){
                                found = i;
                            }
                        }

                        if(found == -1){
                            accounts.add(new MCO2_9_Java_BankAccount(name, number, balance));
                        }
                        else{
                            accounts.get(found).setBalance(balance); // File is in order, so the last row is the latest balance
                        }
                    }
                    catch(NumberFormatException e){
                        // Row has an invalid number - ignore it here
                    }
                }
            }
        }
        catch(IOException e){
            // File not found yet - start with no accounts
        }

        return accounts;
    }

    public long getNextAccountNumber(){
        long highest = firstAccountNumber - 1;
        long current;
        String line;
        String[] columns;

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            br.readLine(); // Skip header

            while((line = br.readLine()) != null){
                columns = line.split(",");

                if(columns.length >= 2){ // Skip malformed rows instead of crashing
                    try{
                        current = Long.parseLong(columns[1].trim());

                        if(current > highest){
                            highest = current;
                        }
                    }
                    catch(NumberFormatException e){
                        // Row has an invalid account number - ignore it here
                    }
                }
            }
        }
        catch(IOException e){
            // File not found yet (or unreadable) - start from the first account number
        }

        return highest + 1;
    }

    public boolean accountNameExists(String accountName){
        String line;
        String[] columns;

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            br.readLine(); // Skip header

            while((line = br.readLine()) != null){
                columns = line.split(",");

                if(columns.length >= 1 && columns[0].trim().equalsIgnoreCase(accountName)){
                    return true;
                }
            }
        }
        catch(IOException e){
            // File not found yet - no accounts exist
        }

        return false;
    }

    // Returns true if the row was saved successfully
    public boolean saveTransaction(String accountName, long accountNumber, String type, double amount, double balance){
        File file = new File(fileName);
        boolean needsHeader = !file.exists() || file.length() == 0;
        LocalDateTime now = LocalDateTime.now();
        String row;

        row = accountName + "," + accountNumber + ","
                + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ","
                + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ","
                + "PHP,"
                + String.format(Locale.US, "%.2f", amount) + ","
                + type + ","
                + String.format(Locale.US, "%.2f", balance);

        try{
            // Append mode, so existing rows are kept
            try(FileWriter fw = new FileWriter(file, true)){
                if(needsHeader){
                    fw.write(header + "\r\n");
                }
                else if(!endsWithNewline(file)){
                    fw.write("\r\n"); // Keep the new row from joining the last row
                }

                fw.write(row + "\r\n"); // \r\n to match the line endings of the provided file
            }
            return true;
        }
        catch(IOException e){
            System.out.println("Error: could not save to " + fileName + ".");
            return false;
        }
    }

    private boolean endsWithNewline(File file) throws IOException{
        try(RandomAccessFile raf = new RandomAccessFile(file, "r")){
            raf.seek(raf.length() - 1);
            return raf.read() == '\n';
        }
    }

    // FileHandler Attributes
    private String fileName;
    private String header;
    private long firstAccountNumber;

}