/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// Assumption is the money being stored is in PHP.

import java.util.*;
import java.text.DecimalFormat; // to affix 2 decimal place display
import java.io.File;

public class MCO2_9_Java {
    public static void main(String[] args){

        // Variable declarations
        int     mainMenuChoice, curChoice, numDays, sourceCurrency,
                exchangeCurrency, i, accIndex, reportChoice;

        long    accountNumber;

        String  accountName, currency, returnToMenu, repeatChoice, convertAnother;

        double  balance, eodBalance, initialDeposit, depositAmount, withdrawAmount,
                sourceAmount, sourceAmountPHP, exchangeAmount, newRate, dailyInterest;

        boolean running, runningChoice1, runningChoice2, runningChoice3, runningChoice4, runningChoice7,
                validChar, validCharEXC, validRepeat, validCurrency, validSourceCurrency, validExchangeCurrency,
                validNumDays, validAmount, dataLoaded;

        MCO2_9_Java_Converter conv = new MCO2_9_Java_Converter();

        MCO2_9_Java_FileHandler fh = new MCO2_9_Java_FileHandler();

        MCO2_9_Java_DataLoader dl = new MCO2_9_Java_DataLoader("bank_transactions.csv");

        MCO2_9_Java_ReportGenerator rg;

        ArrayList<MCO2_9_Java_BankAccount> record = fh.loadAccounts(); // For storing accounts

        ArrayList<MCO2_9_Java_Transaction> transactions = new ArrayList<>(); // For cleaned PHP rows for reports

        Scanner sc = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("0.00"); // affix 2 decimal place display

        // Balance and currency set at default values
        balance = 1000;
        currency = "PHP";
        initialDeposit = 0;

        // Formally initialized, to be modified in program.
        depositAmount = 0;
        withdrawAmount = 0;
        curChoice = 0; // Selected currency in menu choice 5
        sourceCurrency = 0;
        sourceAmount = 0;
        sourceAmountPHP = 0; // Source amount in PHP
        exchangeCurrency = 0;
        exchangeAmount = 0;
        newRate = 0; // New exchange rate in menu choice 5
        dataLoaded = false;

        running = true;

        // Warn user if csv file is missing.
        if(!new File("bank_transactions.csv").exists()){
            System.out.println("Warning: bank_transactions.csv was not found in the current folder.");
            System.out.println("The program will start with no existing accounts.\n");
            // a new bank_transactions.csv file will be made when an account is registered.
        }

        // Code proper
        while(running){
            // Reset certain variables for multiple reuse:
            accIndex = -1; // account index
            numDays = 0; // number of days in Show Interest Amount
            validChar = false; // for "Back to Main Menu (Y/N)" choice
            validCurrency = false; // for "Select Foreign Currency" choice
            validNumDays = false; // for "Total Number of Days" choice
            validAmount = false; // for handling numerical inputs
            runningChoice4 = true; // for when another currency conversion transaction happens in menu choice 4
            validRepeat = false; // for when user is prompted to repeat a menu choice (ex. Register another account?)

            // Main Menu ----------------------------------------------------------------------------------------------
            System.out.println("Select Transaction:");
            System.out.println("[1] Register Account");
            System.out.println("[2] Deposit Amount");
            System.out.println("[3] Withdraw Amount");
            System.out.println("[4] Currency Exchange");
            System.out.println("[5] Record Exchange Rates");
            System.out.println("[6] Show Interest Amount");
            System.out.println("[7] Generate Reports");
            System.out.print("\nChoice: ");

            try{
                mainMenuChoice = Integer.parseInt(sc.nextLine());
            }
            catch(NumberFormatException e){ // If user doesn't enter a number
                mainMenuChoice = -1; // Send user to the else statement at the very bottom
            }

            if(mainMenuChoice == 1){
                // Register Account -----------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                runningChoice1 = true;

                while(runningChoice1){
                    validRepeat = false; // Reset variables for proper reuse
                    validAmount = false;

                    System.out.println("\nRegister Account");
                    System.out.print("Account Name: ");
                    accountName = sc.nextLine().trim().replaceAll("\\s+", " "); // Remove extra spaces

                    if(accountName.split(" ").length < 2){
                        System.out.println("Account name must be in the format: First name Last name.");
                    }
                    else if(accountName.contains(",")){
                        System.out.println("Account name must not contain commas.");
                    }
                    else if(fh.accountNameExists(accountName)){
                        System.out.println("Account name is already registered.");
                    }
                    else{
                        while(!validAmount){
                            try{
                                System.out.print("Initial Deposit: ");
                                initialDeposit = Math.round(Double.parseDouble(sc.nextLine()) * 100) / 100.0; // 2 decimals

                                if(initialDeposit < 5000){
                                    System.out.println("Initial deposit must be at least 5000.00, please try again.");
                                }
                                else{
                                    validAmount = true;
                                }
                            }
                            catch(NumberFormatException e){
                                System.out.println("Invalid input, please try again.");
                            }
                        }

                        accountNumber = fh.getNextAccountNumber();

                        if(fh.saveTransaction(accountName, accountNumber, "deposit", initialDeposit, initialDeposit)){
                            record.add(new MCO2_9_Java_BankAccount(accountName, accountNumber, initialDeposit));

                            System.out.println("\nAccount created successfully!");
                        }
                    }

                    while(!validRepeat){ // Option to go register another account
                        System.out.print("\nRegister another account? (Y/N): ");
                        repeatChoice = sc.nextLine();

                        if(repeatChoice.equalsIgnoreCase("y")){
                            validRepeat = true;
                        }
                        else if(repeatChoice.equalsIgnoreCase("n")){
                            runningChoice1 = false; // Go back to main menu
                            validRepeat = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.");
                        }
                    }
                }
            }

            else if(mainMenuChoice == 2){
                // Deposit Amount -------------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                runningChoice2 = true;

                while(runningChoice2){
                    accIndex = -1; // Reset variables for proper reuse
                    validAmount = false;
                    validRepeat = false;

                    System.out.println("\nDeposit Amount");
                    System.out.print("Account Name: ");
                    accountName = sc.nextLine().trim().replaceAll("\\s+", " ");

                    for(i = 0; i < record.size(); i++){
                        if(accountName.equalsIgnoreCase(record.get(i).getAccountName())){
                            balance = record.get(i).getBalance();
                            accIndex = i;
                        }
                    }

                    if(accIndex == -1){
                        System.out.println("Account not found.");
                    }
                    else{
                        System.out.println("Current Balance: " + df.format(balance));
                        System.out.println("Currency: " + currency);

                        while(!validAmount){
                            try{
                                System.out.print("\nDeposit Amount: ");
                                depositAmount = Math.round(Double.parseDouble(sc.nextLine()) * 100) / 100.0; // 2 decimals

                                if(depositAmount <= 0){
                                    System.out.println("Deposit amount must be greater than 0, please try again.");
                                }
                                else{
                                    validAmount = true;
                                }
                            }
                            catch(NumberFormatException e){
                                System.out.println("Invalid input, please try again.");
                            }
                        }

                        balance = Math.round((balance + depositAmount) * 100) / 100.0;

                        // Save to the file first, then update the account in memory
                        if(fh.saveTransaction(record.get(accIndex).getAccountName(), record.get(accIndex).getAccountNumber(), "deposit", depositAmount, balance)){
                            record.get(accIndex).setBalance(balance);

                            System.out.println("\nUpdated Balance: " + df.format(balance));
                        }
                    }

                    while(!validRepeat){ // Option to deposit again
                        System.out.print("\nDeposit another amount? (Y/N): ");
                        repeatChoice = sc.nextLine();

                        if(repeatChoice.equalsIgnoreCase("y")){
                            validRepeat = true;
                        }
                        else if(repeatChoice.equalsIgnoreCase("n")){
                            runningChoice2 = false; // Go back to main menu
                            validRepeat = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.");
                        }
                    }
                }
            }

            else if(mainMenuChoice == 3){
                // Withdraw Amount ------------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                runningChoice3 = true;

                while(runningChoice3){
                    accIndex = -1; // Reset variables for proper reuse
                    validAmount = false;
                    validRepeat = false;

                    System.out.println("\nWithdraw Amount");
                    System.out.print("Account Name: ");
                    accountName = sc.nextLine().trim().replaceAll("\\s+", " ");

                    for(i = 0; i < record.size(); i++){
                        if(accountName.equalsIgnoreCase(record.get(i).getAccountName())){
                            balance = record.get(i).getBalance();
                            accIndex = i;
                        }
                    }

                    if(accIndex == -1){
                        System.out.println("Account not found.");
                    }
                    else{
                        System.out.println("Current Balance: " + df.format(balance));
                        System.out.println("Currency: " + currency);

                        while(!validAmount){
                            try{
                                System.out.print("\nWithdraw Amount: ");
                                withdrawAmount = Math.round(Double.parseDouble(sc.nextLine()) * 100) / 100.0; // 2 decimals

                                if(withdrawAmount <= 0){
                                    System.out.println("Withdraw amount must be greater than 0, please try again.");
                                }
                                else if(withdrawAmount > balance){
                                    System.out.println("Withdraw amount must not be greater than current balance, please try again.");
                                }
                                else{
                                    validAmount = true;
                                }
                            }
                            catch(NumberFormatException e){
                                System.out.println("Invalid input, please try again.");
                            }
                        }

                        balance = Math.round((balance - withdrawAmount) * 100) / 100.0;

                        // Save to the file first, then update the account in memory
                        if(fh.saveTransaction(record.get(accIndex).getAccountName(), record.get(accIndex).getAccountNumber(), "withdrawal", withdrawAmount, balance)){
                            record.get(accIndex).setBalance(balance);

                            System.out.println("\nUpdated Balance: " + df.format(balance));
                        }
                    }

                    while(!validRepeat){ // Option to withdraw again
                        System.out.print("\nWithdraw another amount? (Y/N): ");
                        repeatChoice = sc.nextLine();

                        if(repeatChoice.equalsIgnoreCase("y")){
                            validRepeat = true;
                        }
                        else if(repeatChoice.equalsIgnoreCase("n")){
                            runningChoice3 = false; // Go back to main menu
                            validRepeat = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.");
                        }
                    }
                }
            }

            else if(mainMenuChoice == 4){
                // Currency Exchange ---------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                while(runningChoice4){
                    validCharEXC = false; // Reset variables for proper reuse
                    validAmount = false;
                    validSourceCurrency = false;
                    validExchangeCurrency = false;

                    System.out.println("\nForeign Currency Exchange");
                    System.out.println("Source Currency Option:");
                    System.out.println("[1] Philippine Peso (PHP)");
                    System.out.println("[2] United States Dollar (USD)");
                    System.out.println("[3] Japanese Yen (JPY)");
                    System.out.println("[4] British Pound Sterling (GBP)");
                    System.out.println("[5] Euro (EUR)");
                    System.out.println("[6] Chinese Yuan Renminni (CNY)");

                    while(!validSourceCurrency){
                        try{
                            System.out.print("Source Currency: ");
                            sourceCurrency = Integer.parseInt(sc.nextLine());
                        }
                        catch(NumberFormatException e){
                            sourceCurrency = -1;
                        }
                        if(sourceCurrency >= 1 && sourceCurrency <= 6){
                            validSourceCurrency = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.\n");
                        }
                    }

                    while(!validAmount){
                        try{
                            System.out.print("\nSource Amount: ");
                            sourceAmount = Double.parseDouble(sc.nextLine());

                            if(sourceAmount <= 0){
                                System.out.println("Source amount must be greater than 0, please try again.");
                            }
                            else{
                                validAmount = true;
                            }
                        }
                        catch(NumberFormatException e){
                            System.out.println("Invalid input, please try again.");
                        }
                    }

                    // Convert currency to base PHP first - for easier conversion later.
                    sourceAmountPHP = conv.convertToPHP(sourceCurrency, sourceAmount);

                    System.out.println("\nExchanged Currency Options:");
                    System.out.println("[1] Philippine Peso (PHP)");
                    System.out.println("[2] United States Dollar (USD)");
                    System.out.println("[3] Japanese Yen (JPY)");
                    System.out.println("[4] British Pound Sterling (GBP)");
                    System.out.println("[5] Euro (EUR)");
                    System.out.println("[6] Chinese Yuan Renminni (CNY)");

                    while(!validExchangeCurrency){
                        try{
                            System.out.print("Exchange Currency: ");
                            exchangeCurrency = Integer.parseInt(sc.nextLine());
                        }
                        catch(NumberFormatException e){
                            exchangeCurrency = -1;
                        }
                        if(exchangeCurrency >= 1 && exchangeCurrency <= 6){
                            validExchangeCurrency = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.\n");
                        }
                    }

                    // Conversion to desired currency and display of exchange amount
                    exchangeAmount = conv.convertFromPHP(exchangeCurrency, sourceAmountPHP);
                    System.out.println("Exchange Amount: " + df.format(exchangeAmount));

                    while(!validCharEXC){
                        System.out.print("\nConvert another currency (Y/N)? ");
                        convertAnother = sc.nextLine();

                        if(convertAnother.equalsIgnoreCase("y")){
                            validCharEXC = true;
                        }
                        else if(convertAnother.equalsIgnoreCase("n")){
                            runningChoice4 = false; // Stop converting currency - proceed to "back to main menu" choice
                            validCharEXC = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.");
                        }
                    }
                }
                while(!validChar){
                    System.out.print("\nBack to the Main Menu (Y/N): ");
                    returnToMenu = sc.nextLine();

                    if(returnToMenu.equalsIgnoreCase("y")){
                        validChar = true;
                    }
                    else if(returnToMenu.equalsIgnoreCase("n")){
                        running = false; // End program if user doesn't want to go back to main menu.
                        validChar = true;
                    }
                    else{
                        System.out.println("Invalid input, please try again.");
                    }
                }
            }

            else if(mainMenuChoice == 5){
                // Record Exchange Rate -------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                System.out.println("\nRecord Exchange Rate");
                System.out.println("\n[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)");
                System.out.println("[3] Japanese Yen (JPY)\n[4] British Pound Sterling (GBP)");
                System.out.println("[5] Euro (EUR)\n[6] Chinese Yuan Renminni (CNY)");

                while(!validCurrency){
                    try {
                        System.out.print("\nSelect Foreign Currency: ");
                        curChoice = Integer.parseInt(sc.nextLine());
                    }
                    catch (NumberFormatException e){
                        curChoice = -1;
                    }

                    if(curChoice >= 1 && curChoice <= 6){
                        validCurrency = true;
                    }
                    else{
                        System.out.println("Invalid choice, please try again.");
                    }
                }

                if(curChoice == 1){
                    // PHP is the base currency, so its rate always stays at 1.
                    System.out.println("Exchange Rate: " + df.format(conv.getRate(curChoice)));
                    System.out.println("PHP is the base currency, its exchange rate cannot be changed.");
                }
                else{
                    System.out.println("Current Exchange Rate: " + df.format(conv.getRate(curChoice)));

                    while(!validAmount){
                        try{
                            System.out.print("\nNew Exchange Rate: ");
                            newRate = Double.parseDouble(sc.nextLine());

                            if(newRate <= 0){
                                System.out.println("Exchange rate must be greater than 0, please try again.");
                            }
                            else{
                                validAmount = true;
                            }
                        }
                        catch(NumberFormatException e){
                            System.out.println("Invalid input, please try again.");
                        }
                    }

                    conv.setRate(curChoice, newRate);

                    System.out.println("Exchange Rate: " + df.format(conv.getRate(curChoice)));
                }

                while(!validChar){
                    System.out.print("\nBack to the Main Menu (Y/N): ");
                    returnToMenu = sc.nextLine();

                    if(returnToMenu.equalsIgnoreCase("y")){
                        validChar = true;
                    }
                    else if(returnToMenu.equalsIgnoreCase("n")){
                        running = false; // End program if user doesn't want to go back to main menu.
                        validChar = true;
                    }
                    else{
                        System.out.println("Invalid input, please try again.");
                    }
                }
            }

            else if(mainMenuChoice == 6){
                // Show Interest Amount -------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                System.out.println("\nShow Interest Amount");
                System.out.print("Account Name: ");
                accountName = sc.nextLine().trim().replaceAll("\\s+", " ");

                for(i = 0; i < record.size(); i++){
                    if(accountName.equalsIgnoreCase(record.get(i).getAccountName())){
                        balance = record.get(i).getBalance();
                        accIndex = i;
                    }
                }

                if(accIndex == -1){
                    System.out.println("Account not found.");
                }

                else{
                    eodBalance = record.get(accIndex).getBalance(); // since we're only displaying, original balance remains untouched.

                    System.out.println("\nCurrent Balance: " + df.format(balance));
                    System.out.println("Currency: " + currency);
                    System.out.println("Interest Rate: 5%\n");

                    while(!validNumDays){
                        try{
                            System.out.print("Total Number of Days: ");
                            numDays = Integer.parseInt(sc.nextLine());
                        }
                        catch(NumberFormatException e){
                            numDays = -1;
                        }

                        if(numDays > 0 && numDays < 32){
                            validNumDays = true;
                        }
                        else{
                            System.out.println("Invalid input, please try again.\n");
                        }
                    }

                    System.out.println("Day | Interest | Balance |");
                    for(i = 1; i <= numDays; i++){
                        // Daily Interest = End-of-Day Balance x (Annual Rate / 365)
                        dailyInterest = Math.round(eodBalance * (0.05 / 365) * 100) / 100.0;
                        eodBalance = eodBalance + dailyInterest;

                        System.out.printf("%-3d | %-8s | %-7s |\n", i, df.format(dailyInterest), df.format(eodBalance));
                    }

                }

                while(!validChar){
                    System.out.print("\nBack to the Main Menu (Y/N): ");
                    returnToMenu = sc.nextLine();

                    if(returnToMenu.equalsIgnoreCase("y")){
                        validChar = true;
                    }
                    else if(returnToMenu.equalsIgnoreCase("n")){
                        running = false; // End program if user doesn't want to go back to main menu.
                        validChar = true;
                    }
                    else{
                        System.out.println("Invalid input, please try again.");
                    }
                }
            }

            else if(mainMenuChoice == 7){
                // Generate Reports -----------------------------------------------------------------------------------
                System.out.println("Choice = " + mainMenuChoice);

                runningChoice7 = true;

                while(runningChoice7){
                    System.out.println("\nGenerate Reports");
                    System.out.println("\n[1] Load the file");
                    System.out.println("[2] Generate Reports");
                    System.out.println("[3] Back to the Main Menu");

                    try{
                        System.out.print("Enter choice: ");
                        reportChoice = Integer.parseInt(sc.nextLine());
                    }
                    catch(NumberFormatException e){
                        reportChoice = -1;
                    }

                    if(reportChoice == 1){
                        transactions = dl.load(); // Read, validate, clean, and filter the file (REQ-0003 to REQ-0005)

                        if(!dl.isFileFound()){
                            dataLoaded = false;
                            System.out.println("\nError: bank_transactions.csv was not found.");
                        }
                        else{
                            dataLoaded = true;
                            System.out.println("\nProcessing dataset... (" + String.format("%,d", dl.getTotalRows()) + " rows loaded, "
                                    + String.format("%,d", transactions.size()) + " filtered for PHP currency)");
                            System.out.println(dl.getInvalidRows() + " invalid row(s) removed, " + dl.getNonPhpRows()
                                    + " non-PHP row(s) excluded. Details saved to " + dl.getLogFileName());
                        }
                    }
                    else if(reportChoice == 2){
                        if(!dataLoaded){
                            System.out.println("\nPlease load the file first (option 1).");
                        }
                        else{
                            System.out.println("\nGenerating reports...");
                            System.out.println("Outputs saved to individual files...");

                            rg = new MCO2_9_Java_ReportGenerator(transactions);
                            rg.generateReport1(); // REQ-0006
                            rg.generateReport2(); // REQ-0007
                            rg.generateReport3(); // REQ-0008
                            rg.generateReport4(); // REQ-0009 (own report - top 10 transactions)
                            rg.generateSummary(); // REQ-0010 summary.json

                            runningChoice7 = false; // Go back to main menu
                        }
                    }
                    else if(reportChoice == 3){
                        runningChoice7 = false; // Go back to main menu
                    }
                    else{
                        System.out.println("Invalid input, please try again.");
                    }
                }
            }

            else{ // User enters invalid input (whether out of range index or not a numerical input)
                System.out.println("Invalid input, please try again.\n");
            }
        }
        sc.close(); // close scanner
    }
}
