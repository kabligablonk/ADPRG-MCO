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

    // Constructors
    public MCO1_9_Java_Converter(){

        // Default exchange rates:
        // Index order follows the menu: 0 = PHP, 1 = USD, 2 = JPY, 3 = GBP, 4 = EUR, 5 = CNY
        rates = new double[]{1.00, 62.00, 0.40, 84.00, 72.00, 9.00};
    }

    // Methods
    public double getRate(int currency){ // currency is the menu choice (1-6)
        return rates[currency - 1];
    }

    public void setRate(int currency, double rate){
        rates[currency - 1] = rate;
    }

    public double convertToPHP(int sourceCurrency, double sourceAmount){
        return sourceAmount * rates[sourceCurrency - 1];
    }

    public double convertFromPHP(int exchangeCurrency, double sourceAmountPHP){
        return sourceAmountPHP / rates[exchangeCurrency - 1];
    }

    // Converter Attributes
    private double[] rates;

}
