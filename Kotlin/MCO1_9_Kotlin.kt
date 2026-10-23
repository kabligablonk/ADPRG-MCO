/****************
 * Last Name: Covar, Lising, Miranda, Tiotuyco
 * Language: Kotlin
 * Paradigm(s): Procedural Programming
 */
import java.util.Scanner
import java.util.Locale

fun main() {
    // variable declarations
    var choiceM: Int? = null // main menu selection
    var Echoice: String? = null // Y/N selection
    var ExChoice: Int? = null // exchange currency choice
    var choiceER: Int? = null // exchange rate choice
    var Schoice: Int? = null // source exchange rate choice
    var amount: Double? = null // amount to deposit/withdraw
    var name: String? = null // registered name
    var sAmount: Double? = null // source amount to be exchanged
    val kb = Scanner(System.`in`) // scanner
    var running = true //program loop
    var choiceRunning = true // menu loop
    var balance = 1000.00 // balance
    var nDays: Int? = null // days for interest

    val eRate = arrayOfNulls<Double>(5) // exchange rate storage

    while(running) {
        while(choiceRunning) {
            // Main Menu
            println("Select Transaction:")
            println(
                "[1] Register Account Name\n[2] Deposit Amount\n[3] Withdraw Amount\n[4] Currency Exchange\n" +
                        "[5] Record Exchange Rates\n[6]Show Interest Amount\n[7] Exit"
            )
            println("Choice: ")

            choiceM = kb.nextInt()

            println("***\nChoice: $choiceM")

            when (choiceM) {
                1 -> {
                    // Register Account Name
                    println("Register Account Name")
                    println("Account Name: ")
                    name = readln()
                    println("Account Name = $name\n")
                    println("\nBack to Main Menu (Y/N):")
                    Echoice = readln()
                    if (Echoice == "N" || Echoice == "n") {
                        choiceRunning = false
                        running = false
                    }
                }

                2 -> {
                    // Deposit Amount
                    println("Deposit Amount\nAccount Name: $name\n")
                    println("Current Balance: $balance\nCurrency: PHP\nDeposit Amount: ")
                    amount = kb.nextDouble()
                    balance = balance + amount
                    println("Updated Balance: $balance")
                    println("\nBack to Main Menu (Y/N):")
                    Echoice = readln()
                    if (Echoice == "N" || Echoice == "n") {
                        choiceRunning = false
                        running = false
                    }
                }

                3 -> {
                    // Withdraw Amount
                    println("Withdraw Amount\nAccount Name: $name\n")
                    println("Current Balance: $balance\nCurrency: PHP\nWithdraw Amount: ")
                    amount = kb.nextDouble()
                    balance = balance - amount
                    println("Updated Balance: $balance")
                    println("\nBack to Main Menu (Y/N):")
                    Echoice = readln()
                    if (Echoice == "N" || Echoice == "n") {
                        choiceRunning = false
                        running = false
                    }
                }

                5 -> {
                    // Record Exchange Rate
                    println(
                        "Record Exchange Rates\n\n[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)\n[3] Japanese Yen (JPY)\n" +
                                "[4] British Pound Sterling\n[5] Euro (EUR)\n[6] Chinese Yuan Renminni"
                    )
                    println("Select Exchange Rate: ")
                    choiceER = kb.nextInt()
                    println("Exchange Rate: ")
                    eRate[choiceER] = kb.nextDouble()
                    println("\nBack to Main Menu (Y/N):")
                    Echoice = readln()
                    if (Echoice == "N" || Echoice == "n") {
                        choiceRunning = false
                        running = false
                    }
                }

                4 -> {
                    // Currency Exchange
                    println("Foreign Currency Exchange\nSource Currency Option:\n[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)\n[3] Japanese Yen (JPY)\n" +
                                                        "[4] British Pound Sterling (GBP)\n[5] Euro (EUR)\n[6] Chinese Yuan Renminbi (CNY)\n")
                    println("Select Source Currency: ")
                    Schoice= kb.nextInt()
                    println("Source Amount: ")
                    sAmount = kb.nextDouble()

                    println("Exchange Currency Option:\n[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)\n[3] Japanese Yen (JPY)\n" +
                            "[4] British Pound Sterling (GBP)\n[5] Euro (EUR)\n[6] Chinese Yuan Renminbi (CNY)\n")
                    println("Exchange Currency:")
                    ExChoice = kb.nextInt()

                    val sRate = when (Schoice) {
                        1 -> 1.00
                        2 -> 62.00
                        3 -> 0.40
                        4 -> 84.00
                        5 -> 72.00
                        else -> 9.00
                    }
                    val ExRate = when (ExChoice) {
                        1 -> 1.00
                        2 -> 62.00
                        3 -> 0.40
                        4 -> 84.00
                        5 -> 72.00
                        else -> 9.00
                    }

                    val exchanged = sAmount * sRate / ExRate
                    println("Exchange Amount: $exchanged")

                    println("\nBack to Main Menu (Y/N):")
                    Echoice = readln()
                    if (Echoice == "N" || Echoice == "n") {
                        choiceRunning = false
                        running = false
                    }
                }

                6 -> {
                    // Show Interest Amount
                    println("Show Interest Amount")
                    println("Account Name: $name\n")
                    println("Current Balance: $balance\nCurrency: PHP\nInterest Rate: 5%")
                    println("Total Number of Days: ")
                    nDays = kb.nextInt()


                    println("Day | Interest | Balance |")
                    for(i in 1..nDays) {
                        var interest = balance * 0.05/365
                        balance = balance + interest
                        val formattedBalance = String.format(Locale.US, "%.2f",balance)
                        val formattedInterest = String.format(Locale.US, "%.2f", interest)
                        println("$i | $formattedInterest | $formattedBalance")
                    }
                    println("\nBack to Main Menu (Y/N):")
                    Echoice = readln()
                    if (Echoice == "N" || Echoice == "n") {
                        choiceRunning = false
                        running = false
                    }
                }

                7 -> {
                    // Exit
                    choiceRunning = false
                    running = false
                }
            }
        }
    }
}
