/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: Java
* Paradigm(s): Imperative programming
*******************
*/

import java.util.*;
import java.text.DecimalFormat; // to affix 2 decimal place display

public class MCO1_BasicIO_9_Java {
    public static void main(String[] args){

        // Variable declarations
        int mainMenuChoice, curChoice;
        String accountName, currency;
        double balance, depositAmount, withdrawAmount, sourceAmount,
                exchRate, usdRate, jpyRate, gbpRate, eurRate, cnyRate;

        Scanner sc = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("0.00"); // affix 2 decimal place display

        // balance and currency set at default values
        balance = 1000;
        currency = "PHP";

        // Foreign exchange rates
        usdRate = 62.00;
        jpyRate = 0.40;
        gbpRate = 84.00;
        eurRate = 72.00;
        cnyRate = 9.00;

        // Code proper - No loops, conditions, and input validation yet (to be added in following milestones)
        // Interface Requirement 1: Main Menu -------------------------------------------------------------------------
        System.out.println("Select Transaction:");
        System.out.println("[1] Register Account Name");
        System.out.println("[2] Deposit Amount");
        System.out.println("[3] Withdraw Amount");
        System.out.println("[4] Currency Exchange");
        System.out.println("[5] Record Exchange Rates");
        System.out.println("[6] Show Interest Amount");
        System.out.print("\nChoice: ");

        mainMenuChoice = Integer.parseInt(sc.nextLine());

        System.out.println("\n***");
        System.out.println("Choice = " + mainMenuChoice);

        // Interface Requirement 2: Register Account Name -------------------------------------------------------------
        System.out.println("\nRegister Account Name");
        System.out.print("Account Name: ");

        accountName = sc.nextLine();

        System.out.println("\n***");
        System.out.println("Account Name = " + accountName);

        // Interface Requirement 3: Deposit Amount --------------------------------------------------------------------
        System.out.println("\nDeposit Amount");
        System.out.print("Account Name: ");
        accountName = sc.nextLine();

        System.out.println("Current Balance: " + df.format(balance));
        System.out.println("Currency: " + currency);

        System.out.print("\nDeposit Amount: ");
        depositAmount = Double.parseDouble(sc.nextLine());

        System.out.println("\n***");
        System.out.println("Account Name = " + accountName);
        System.out.println("Deposit Amount = " + df.format(depositAmount));

        // Interface Requirement 4: Withdraw Amount -------------------------------------------------------------------
        System.out.println("\nWithdraw Amount");
        System.out.print("Account Name: ");
        accountName = sc.nextLine();

        System.out.println("Current Balance: " + df.format(balance));
        System.out.println("Currency: " + currency);

        System.out.print("\nWithdraw Amount: ");
        withdrawAmount = Double.parseDouble(sc.nextLine());

        System.out.println("\n***");
        System.out.println("Account Name = " + accountName);
        System.out.println("Withdraw Amount = " + df.format(withdrawAmount));

        // Interface Requirement 5: Record Exchange Rate --------------------------------------------------------------
        System.out.println("\nRecord Exchange Rate");
        System.out.println("\n[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)");
        System.out.println("[3] Japanese Yen (JPY)\n[4] British Pound Sterling (GBP)");
        System.out.println("[5] Euro (EUR)\n[6] Chinese Yuan Renminni (CNY)");

        System.out.print("\nSelect Foreign Currency: ");
        curChoice = Integer.parseInt(sc.nextLine());

        System.out.print("Exchange Rate: ");
        exchRate = Double.parseDouble(sc.nextLine());

        System.out.println("\n***");
        System.out.println("Select Foreign Currency = [" + curChoice + "]");
        System.out.println("Exchange Rate = " + df.format(exchRate));
        
        // Interface Requirement 6: Currency Exchange -----------------------------------------------------------------
        System.out.println("\nForeign Currency Exchange");
        System.out.print("Source Amount: ");
        sourceAmount = Double.parseDouble(sc.nextLine());

        System.out.println("\nExchanged Currency");
        System.out.println("[1] Philippine Peso (PHP) = " + df.format(sourceAmount));
        System.out.println("[2] United States Dollar (USD) = " + df.format(sourceAmount * usdRate));
        System.out.println("[3] Japanese Yen (JPY) = " + df.format(sourceAmount * jpyRate));
        System.out.println("[4] British Pound Sterling (GBP) = " + df.format(sourceAmount * gbpRate));
        System.out.println("[5] Euro (EUR) = " + df.format(sourceAmount * eurRate));
        System.out.println("[6] Chinese Yuan Renminni (CNY) = " + df.format(sourceAmount * cnyRate));

        System.out.println("\n***");
        System.out.println("Target Currency = Philippine Peso (PHP)");
        System.out.println("Source Amount (PHP) = " + df.format(sourceAmount));

        sc.close(); // close scanner - end of program.

    }
}
