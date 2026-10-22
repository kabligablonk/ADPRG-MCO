/*
 *******************
 * Last names: Covar, Lising, Miranda, Tiotuyco
 * Language: Java
 * Paradigm(s): Imperative programming, Object-Oriented Programming
 *******************
 */

// The original name of this file/class is "Converter" - followed file naming convention as stated in Canvas
// Java's main/program proper is located in "MCO1_9_Java.java"

public class MCO1_9_Java_Converter {

    // Methods
    public double convertToPHP(int sourceCurrency, double sourceAmount){
        if(sourceCurrency == 2){ // USD
            return sourceAmount * 62.00;
        }
        else if(sourceCurrency == 3){ // JPY
            return sourceAmount * 0.40;
        }
        else if(sourceCurrency == 4){ // GBP
            return sourceAmount * 84.00;
        }
        else if(sourceCurrency == 5){ // EUR
            return sourceAmount * 72.00;
        }
        else if(sourceCurrency == 6){ // CNY
            return sourceAmount * 9.00;
        }
        else{ // PHP
            return sourceAmount;
        }
    }

    public double convertFromPHP(int exchangeCurrency, double sourceAmountPHP){
        if(exchangeCurrency == 2){ // USD
            return sourceAmountPHP / 62.00;
        }
        else if(exchangeCurrency == 3){ // JPY
            return sourceAmountPHP / 0.40;
        }
        else if(exchangeCurrency == 4){ // GBP
            return sourceAmountPHP / 84.00;
        }
        else if(exchangeCurrency == 5){ // EUR
            return sourceAmountPHP / 72.00;
        }
        else if(exchangeCurrency == 6){ // CNY
            return sourceAmountPHP / 9.00;
        }
        else{ // PHP
            return sourceAmountPHP;
        }
    }
}
