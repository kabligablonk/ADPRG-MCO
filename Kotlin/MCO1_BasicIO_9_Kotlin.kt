/****************
 * Last Name: Covar, Lising, Miranda, Tiotuyco
 * Language: Kotlin
 * Paradigm(s): Procedural Programming
 */
import java.util.Scanner

fun main() {
    var choice: Int? = null;
    var amount: Double? = null;
    var name: String? = null;
    var eRate: Double? = null;
    var sAmount: Double? = null;
    val kb = Scanner(System.`in`);

    println("Select Transaction:")
    println("[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount\n[4] Currency Exchange\n" +
            "[5] Record Exchange Rates\n[6]Show Interest Amount")
    println("Choice: ")

    choice = kb.nextInt()

    println("***\nChoice: $choice")

    println("Register Account Name")
    println("Account Name: ")
    name = readln()
    println("Account Name = $name\n")

    println("Deposit Amount\nAccount Name: ")
    name = readln()
    println("Current Balance: 1000.00\nCurrency: PHP\nDeposit Amount: ")
    amount = kb.nextDouble()
    println("***\nAccount Name = $name\nDeposit Amount = $amount\n")

    println("Withdraw Amount\nAccount Name: ")
    name = readln()
    println("Current Balance: 1000.00\nCurrency: PHP\nWithdraw Amount: ")
    amount = kb.nextDouble()
    println("***\nAccount Name = $name\nWithdraw Amount = $amount\n")

    println("Record Exchange Rates\n\n[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)\n[3] Japanese Yen (JPY)\n" +
            "[4] British Pound Sterling\n[5] Euro (EUR)\n[6] Chinese Yuan Renminni")
    println("Select Exchange Rate: ")
    choice = kb.nextInt()
    println("Exchange Rate: ")
    eRate = kb.nextDouble()
    println("***\nSelect Foreign Currency = [$choice]\nExchange Rate = $eRate\n")

    println("Foreign Currency Exchange\nSource Amount (PHP): ")
    sAmount = kb.nextDouble()
    val USAmount = sAmount * 62.00
    val JPAmount = sAmount * 0.40
    val GBAmount = sAmount * 84.00
    val EUAmount = sAmount * 72.00
    val CNAmount = sAmount * 9.00
    println("Exchanged Currency\n[1] Philippine Peso (PHP) = $sAmount\n[2] United States Dollar (USD) = $USAmount\n[3] Japanese Yen (JPY) = $JPAmount\n" +
            "[4] British Pound Sterling (GBP) = $GBAmount\n[5] Euro (EUR) = $EUAmount\n[6] Chinese Yuan Renminni (CNY) = $CNAmount\n")
    println("***\nSource Currency = Philippine Peso (PHP)\n Source Amount (PHP) = $sAmount")
}