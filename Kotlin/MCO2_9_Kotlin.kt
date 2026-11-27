/****************
 * Last Name: Covar, Lising, Miranda, Tiotuyco
 * Language: Kotlin
 * Paradigm(s): Procedural Programming
 */
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

// variable declaration
const val CSV_FILE = "bank_transactions.csv"
const val CSV_HEADER = "account_name,account_number,date,time,currency,amount,type,current_balance"
const val MIN_INITIAL_DEPOSIT = 5000.00
const val FIRST_ACCOUNT_NUMBER = 1092000001L


const val ANNUAL_INTEREST_RATE = 0.05
const val REPORT1_MIN_ADB = 100000.00
const val SILVER_LIMIT = 500000.00
const val GOLD_LIMIT = 1000000.00
const val REPORT2_MIN_BALANCE = 10000.00
const val PROJECTION_DAYS = 30
const val SUSPICIOUS_WITHDRAWAL = 500000.00
const val SUSPICIOUS_TURNOVER = 90.0
const val PREVIEW_ROWS = 2

const val REPORT1_FILE = "report1_banking_privilege_tiers.csv"
const val REPORT2_FILE = "report2_interest_expense_projection.csv"
const val REPORT3_FILE = "report3_suspicious_client_behavior.csv"
const val REPORT4_FILE = "report4_monthly_cash_flow_summary.csv"
const val SUMMARY_FILE = "summary.json"

val DATE_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)
val TIME_IN_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm:ss").withResolverStyle(ResolverStyle.STRICT) // reads 8:22:00 and 08:22:00
val TIME_OUT_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss") // REQ-0002: hh:mm:ss, 24-hour

data class Txn(
    val name: String,
    val accountNumber: Long,
    val date: LocalDate,
    val time: LocalTime,
    val currency: String,
    val type: String,
    val amount: Double,
    val balance: Double
)

data class IngestResult(val found: Boolean, val totalRows: Int, val txns: List<Txn>, val errors: List<String>)

data class Report1Row(val name: String, val accountAge: Long, val currentBalance: Double,
                      val avgDailyBalance: Double, val tier: String)
data class Report2Row(val name: String, val currentBalance: Double, val expectedInterest: Double)
data class Report3Row(val name: String, val date: LocalDate, val totalWithdrawals: Double, val numDeposits: Int,
                      val totalDeposits: Double, val turnover: Double?, val risk: String)
data class Report4Row(val month: YearMonth, val numDeposits: Int, val totalDeposits: Double,
                      val numWithdrawals: Int, val totalWithdrawals: Double, val netCashFlow: Double,
                      val endBalance: Double)

fun money(x: Double): String = String.format(Locale.US, "%.2f", x)
fun round2(x: Double): Double = Math.round(x * 100.0) / 100.0

fun cnum(x: Double): String = String.format(Locale.US, "%,.2f", x)
fun fmtInt(n: Int): String = String.format(Locale.US, "%,d", n)

fun csvField(s: String): String =
    if (s.contains(",") || s.contains("\"")) "\"" + s.replace("\"", "\"\"") + "\"" else s

// reads the csv file
fun ingest(path: String): IngestResult {
    val file = File(path)
    if (!file.exists()) return IngestResult(false, 0, emptyList(), emptyList())

    val dataLines = file.readLines().drop(1) // skip header
    val clean = ArrayList<Txn>()
    val errors = ArrayList<String>()
    var blankLines = 0

    for ((idx, raw) in dataLines.withIndex()) {
        val rowNo = idx + 2 // line number in the file (header is line 1)
        if (raw.isBlank()) { blankLines++; continue }

        val f = raw.split(",").map { it.trim() }
        if (f.size != 8) { errors.add("Row $rowNo: expected 8 fields but found ${f.size}"); continue }

        val name = f[0]; val accStr = f[1]; val dateStr = f[2]; val timeStr = f[3]
        val curStr = f[4]; val amtStr = f[5]; val typeStr = f[6]; val balStr = f[7]

        val missing = listOf("account_name" to name, "account_number" to accStr, "date" to dateStr,
            "time" to timeStr, "currency" to curStr, "amount" to amtStr,
            "type" to typeStr, "current_balance" to balStr).filter { it.second.isEmpty() }.map { it.first }
        if (missing.isNotEmpty()) { errors.add("Row $rowNo: missing value(s): ${missing.joinToString()}"); continue }

        // Philippine Peso only
        if (!curStr.equals("PHP", ignoreCase = true)) continue

        // invalid values
        val acc = accStr.toLongOrNull()
        if (acc == null || accStr.length != 10) { errors.add("Row $rowNo: invalid account number '$accStr'"); continue }
        val date = try { LocalDate.parse(dateStr, DATE_FMT) } catch (e: Exception) { null }
        if (date == null) { errors.add("Row $rowNo: invalid date '$dateStr'"); continue }
        val time = try { LocalTime.parse(timeStr, TIME_IN_FMT) } catch (e: Exception) { null }
        if (time == null) { errors.add("Row $rowNo: invalid time '$timeStr'"); continue }
        val type = typeStr.lowercase()
        if (type != "deposit" && type != "withdrawal") { errors.add("Row $rowNo: invalid transaction type '$typeStr'"); continue }
        val amount = amtStr.toDoubleOrNull()
        if (amount == null || amount.isNaN() || amount <= 0.0) { errors.add("Row $rowNo: invalid amount '$amtStr'"); continue }
        val balance = balStr.toDoubleOrNull()
        if (balance == null || balance.isNaN()) { errors.add("Row $rowNo: invalid balance '$balStr'"); continue }

        clean.add(Txn(name, acc, date, time, "PHP", type, round2(amount), round2(balance)))
    }
    return IngestResult(true, dataLines.size - blankLines, clean, errors)
}

fun printLoadSummary(res: IngestResult) {
    if (!res.found) {
        println("File '$CSV_FILE' not found. No transactions loaded.")
        return
    }
    println("Processing dataset... (${fmtInt(res.totalRows)} rows loaded, ${fmtInt(res.txns.size)} filtered for PHP currency)")
    if (res.errors.isNotEmpty()) { // report rows that were skipped
        println("Rows skipped because of missing/invalid values: ${fmtInt(res.errors.size)}")
        for (e in res.errors.take(20)) println("  $e")
        if (res.errors.size > 20) println("  ... and ${res.errors.size - 20} more")
    }
}

fun findLatest(txns: List<Txn>, acc: Long): Txn? = txns.lastOrNull { it.accountNumber == acc }

fun normalizeName(s: String): String = s.trim().replace(Regex("\\s+"), " ").lowercase()

fun findAccountByName(txns: List<Txn>, name: String): Txn? {
    val key = normalizeName(name)
    if (key.isEmpty()) return null
    val matches = txns.filter { normalizeName(it.name) == key }.map { it.accountNumber }.distinct()
    if (matches.isEmpty()) return null
    var acc = matches[0]
    if (matches.size > 1) {
        println("Multiple accounts found with that name: ${matches.joinToString(", ")}")
        print("Account Number: ")
        val a = readln().trim().toLongOrNull()
        if (a == null || a !in matches) return null
        acc = a
    }
    return findLatest(txns, acc)
}

// next number after the highest existing account number in the file
fun nextAccountNumber(txns: List<Txn>): Long =
    if (txns.isEmpty()) FIRST_ACCOUNT_NUMBER else txns.maxOf { it.accountNumber } + 1

// append one transaction to the CSV
fun saveTransaction(t: Txn) {
    val file = File(CSV_FILE)
    val nl: String
    if (!file.exists() || file.length() == 0L) {
        nl = "\n"
        file.writeText(CSV_HEADER + nl)
    } else {
        val text = file.readText()
        nl = if (text.contains("\r\n")) "\r\n" else "\n"
        if (!text.endsWith("\n")) file.appendText(nl)
    }
    file.appendText(
        "${t.name},${t.accountNumber},${t.date.format(DATE_FMT)},${t.time.format(TIME_OUT_FMT)}," +
                "${t.currency},${money(t.amount)},${t.type},${money(t.balance)}$nl"
    )
}

fun readAmount(): Double? {
    val v = readln().trim().replace(",", "").toDoubleOrNull()
    return if (v == null || v.isNaN() || v.isInfinite()) null else round2(v)
}

// "Register another account? (Y/N):" prompt. Y = repeat, anything else = back to the main menu.
fun askAnother(prompt: String): Boolean {
    println()
    print("$prompt (Y/N): ")
    return readln().trim().equals("Y", ignoreCase = true)
}

// REQ-0001: Register Account
fun registerAccount() {
    println("Register Account")
    print("Account Name: ")
    val name = readln().trim().replace(Regex("\\s+"), " ")
    if (name.split(" ").size < 2 || name.contains(",")) {
        println()
        println("Invalid name. Enter first name and last name (no commas).")
        return
    }
    print("Initial Deposit: ")
    val amount = readAmount()
    println()
    if (amount == null) {
        println("Invalid amount.")
    } else if (amount < MIN_INITIAL_DEPOSIT) {
        println("Initial deposit must be at least Php ${money(MIN_INITIAL_DEPOSIT)}. Account not created.")
    } else {
        val txns = ingest(CSV_FILE).txns
        val accNo = nextAccountNumber(txns)
        saveTransaction(Txn(name, accNo, LocalDate.now(), LocalTime.now().withNano(0),
            "PHP", "deposit", amount, amount))
        println("Account created successfully!")
    }
}

// REQ-0002: Deposit / Withdraw
fun transact(isDeposit: Boolean) {
    println(if (isDeposit) "Deposit Amount" else "Withdraw Amount")
    print("Account Name: ")
    val nameIn = readln()
    val txns = ingest(CSV_FILE).txns // re-read so the balance always matches the file
    val last = findAccountByName(txns, nameIn)
    if (last == null) {
        println()
        println("Account not found.")
        return
    }
    println("Current Balance: ${money(last.balance)}")
    println("Currency: PHP")
    println()
    print(if (isDeposit) "Deposit Amount: " else "Withdraw Amount: ")
    val amount = readAmount()
    println()
    if (amount == null || amount <= 0.0) {
        println("Invalid amount.")
    } else if (!isDeposit && amount > last.balance) {
        println("Insufficient balance. Transaction cancelled.")
    } else {
        val newBal = round2(if (isDeposit) last.balance + amount else last.balance - amount)
        saveTransaction(Txn(last.name, last.accountNumber, LocalDate.now(), LocalTime.now().withNano(0),
            "PHP", if (isDeposit) "deposit" else "withdrawal", amount, newBal))
        println("Updated Balance: ${money(newBal)}")
    }
}



// Transactions grouped per account, in date/time order
fun groupByAccount(txns: List<Txn>): Map<Long, List<Txn>> =
    txns.sortedWith(compareBy<Txn> { it.date }.thenBy { it.time }).groupBy { it.accountNumber }

// End-of-Day Balance = balance after the last transaction made on or before that day.
fun endOfDayBalance(list: List<Txn>, day: LocalDate): Double {
    var bal = 0.0
    for (t in list) {
        if (t.date.isAfter(day)) break
        bal = t.balance
    }
    return bal
}

// Shows the first PREVIEW_ROWS rows as a small table
fun printTable(header: List<String>, rows: List<List<String>>) {
    val shown = rows.take(PREVIEW_ROWS)
    val widths = header.indices.map { i -> maxOf(header[i].length, shown.maxOfOrNull { it[i].length } ?: 0) }
    fun line(cells: List<String>): String = cells.indices.joinToString(" | ") { cells[it].padEnd(widths[it]) }
    println(line(header))
    println(widths.joinToString("-+-") { "-".repeat(it) })
    for (r in shown) println(line(r))
}

fun emitReport(title: String, fileName: String, header: List<String>, rows: List<List<String>>) {
    val sb = StringBuilder()
    sb.append(header.joinToString(",")).append("\n")
    for (r in rows) sb.append(r.joinToString(",") { csvField(it) }).append("\n")
    File(fileName).writeText(sb.toString())

    println(title)
    printTable(header, rows)
    println()
    println("(Full table exported to $fileName)")
    println()
}

// REQ-0006: Report 1 - Banking Privilege Tiers
fun buildReport1(byAcc: Map<Long, List<Txn>>, refDate: LocalDate): List<Report1Row> {
    val monthStart = refDate.withDayOfMonth(1).minusMonths(1)
    val daysInMonth = monthStart.lengthOfMonth()
    val rows = ArrayList<Report1Row>()

    for ((_, list) in byAcc) {
        var sum = 0.0
        for (i in 0 until daysInMonth) {
            sum += endOfDayBalance(list, monthStart.plusDays(i.toLong()))
        }
        val adb = sum / daysInMonth // sum of End-of-Day Balances / total days in the month
        if (adb >= REPORT1_MIN_ADB) {
            val tier = if (adb < SILVER_LIMIT) "Silver" else if (adb < GOLD_LIMIT) "Gold" else "Platinum"
            rows.add(Report1Row(list.first().name, ChronoUnit.DAYS.between(list.first().date, refDate),
                list.last().balance, round2(adb), tier))
        }
    }
    return rows.sortedWith(compareByDescending<Report1Row> { it.avgDailyBalance }.thenBy { it.name })
}

// REQ-0007: Report 2 - Interest Expense Projection
// Daily Interest = End-of-Day Balance x (Annual Rate / 365). The interest is added to the
// balance each day (same as menu option 6), and the 30 daily amounts are totalled.
fun buildReport2(byAcc: Map<Long, List<Txn>>): List<Report2Row> {
    val rows = ArrayList<Report2Row>()
    for ((_, list) in byAcc) {
        val current = list.last().balance
        if (current >= REPORT2_MIN_BALANCE) {
            var bal = current
            var total = 0.0
            for (d in 1..PROJECTION_DAYS) {
                val interest = bal * ANNUAL_INTEREST_RATE / 365
                total += interest
                bal += interest
            }
            rows.add(Report2Row(list.first().name, current, round2(total)))
        }
    }
    return rows.sortedWith(compareByDescending<Report2Row> { it.expectedInterest }.thenBy { it.name })
}

// REQ-0008: Report 3 - Suspicious Client Behavior

fun buildReport3(txns: List<Txn>): List<Report3Row> {
    val rows = ArrayList<Report3Row>()
    val groups = txns.groupBy { Pair(it.accountNumber, it.date) }
    for ((key, list) in groups) {
        val wd = round2(list.filter { it.type == "withdrawal" }.sumOf { it.amount })
        val deposits = list.filter { it.type == "deposit" }
        val dep = round2(deposits.sumOf { it.amount })
        val tv: Double? = if (dep > 0.0) wd / dep * 100.0 else null
        val suspicious = wd >= SUSPICIOUS_WITHDRAWAL ||
                (tv == null && wd > 0.0) ||
                (tv != null && tv >= SUSPICIOUS_TURNOVER)
        rows.add(Report3Row(list.first().name, key.second, wd, deposits.size, dep, tv,
            if (suspicious) "Suspicious" else "Normal"))
    }
    // ascending by Date, then descending by RiskFlagLevel
    return rows.sortedWith(compareBy<Report3Row> { it.date }.thenByDescending { it.risk }.thenBy { it.name })
}

// REQ-0009: Report 4 - Monthly Cash Flow Summary

fun buildReport4(txns: List<Txn>): List<Report4Row> {
    val sorted = txns.sortedWith(compareBy<Txn> { it.date }.thenBy { it.time })
    val months = sorted.groupBy { YearMonth.from(it.date) }
    val latestBalance = HashMap<Long, Double>()
    val rows = ArrayList<Report4Row>()
    for (ym in months.keys.sorted()) {
        val list = months.getValue(ym)
        for (t in list) latestBalance[t.accountNumber] = t.balance
        val dep = list.filter { it.type == "deposit" }
        val wd = list.filter { it.type == "withdrawal" }
        val depTotal = round2(dep.sumOf { it.amount })
        val wdTotal = round2(wd.sumOf { it.amount })
        rows.add(Report4Row(ym, dep.size, depTotal, wd.size, wdTotal, round2(depTotal - wdTotal),
            round2(latestBalance.values.sum())))
    }
    return rows
}

// REQ-0010: summary.json
fun writeSummary(txns: List<Txn>, byAcc: Map<Long, List<Txn>>, refDate: LocalDate,
                 r1: List<Report1Row>, r2: List<Report2Row>, r3: List<Report3Row>) {
    val totalDeposits = money(txns.filter { it.type == "deposit" }.sumOf { it.amount })
    val totalWithdrawals = money(txns.filter { it.type == "withdrawal" }.sumOf { it.amount })
    val totalBalance = money(round2(byAcc.values.sumOf { it.last().balance }))
    val totalInterest = money(round2(r2.sumOf { it.expectedInterest }))
    val reportMonth = YearMonth.from(refDate).minusMonths(1)

    val sb = StringBuilder()
    sb.append("{\n")
    sb.append("  \"total_accounts\": ${byAcc.size},\n")
    sb.append("  \"total_deposits\": $totalDeposits,\n")
    sb.append("  \"total_withdrawals\": $totalWithdrawals,\n")
    sb.append("  \"total_balance\": $totalBalance,\n")
    sb.append("  \"total_interest_expense\": $totalInterest,\n")
    sb.append("  \"total_number_of_transactions\": ${txns.size},\n")
    sb.append("  \"data_as_of\": \"${refDate.format(DATE_FMT)}\",\n")
    sb.append("  \"privilege_report_month\": \"$reportMonth\",\n")
    sb.append("  \"privilege_tiers\": {\n")
    sb.append("    \"silver\": ${r1.count { it.tier == "Silver" }},\n")
    sb.append("    \"gold\": ${r1.count { it.tier == "Gold" }},\n")
    sb.append("    \"platinum\": ${r1.count { it.tier == "Platinum" }}\n")
    sb.append("  },\n")
    sb.append("  \"suspicious_account_days\": ${r3.count { it.risk == "Suspicious" }}\n")
    sb.append("}\n")
    File(SUMMARY_FILE).writeText(sb.toString())

    println("Summary State ($SUMMARY_FILE):")
    println("{\"total_accounts\": ${byAcc.size}, \"total_deposits\": $totalDeposits, " +
            "\"total_withdrawals\": $totalWithdrawals, \"total_balance\": $totalBalance, " +
            "\"total_interest_expense\": $totalInterest}")
}

fun generateReports(txns: List<Txn>) {
    println("Generating reports...")
    println("Outputs saved to individual files...")
    println()

    val byAcc = groupByAccount(txns)
    val refDate = txns.maxOf { it.date }

    val r1 = buildReport1(byAcc, refDate)
    val r2 = buildReport2(byAcc)
    val r3 = buildReport3(txns)
    val r4 = buildReport4(txns)

    emitReport("Report 1: Banking Privilege Tiers", REPORT1_FILE,
        listOf("AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier"),
        r1.map { listOf(it.name, it.accountAge.toString(), cnum(it.currentBalance), cnum(it.avgDailyBalance), it.tier) })

    emitReport("Report 2: Interest Expense Projection", REPORT2_FILE,
        listOf("AccountName", "CurrentBalance", "ExpectedInterest"),
        r2.map { listOf(it.name, cnum(it.currentBalance), cnum(it.expectedInterest)) })

    emitReport("Report 3: Suspicious Client Behavior", REPORT3_FILE,
        listOf("AccountName", "Date", "TotalWithdrawalAmounts", "NumberOfDeposits", "TotalDepositAmounts",
            "TurnoverVelocity(%)", "RiskFlagLevel"),
        r3.map {
            listOf(it.name, it.date.format(DATE_FMT), cnum(it.totalWithdrawals), it.numDeposits.toString(),
                cnum(it.totalDeposits), if (it.turnover == null) "N/A" else cnum(it.turnover), it.risk)
        })

    emitReport("Report 4: Monthly Cash Flow Summary", REPORT4_FILE,
        listOf("Month", "NumberOfDeposits", "TotalDepositAmounts", "NumberOfWithdrawals",
            "TotalWithdrawalAmounts", "NetCashFlow", "EndOfMonthTotalBalance"),
        r4.map {
            listOf(it.month.toString(), it.numDeposits.toString(), cnum(it.totalDeposits),
                it.numWithdrawals.toString(), cnum(it.totalWithdrawals), cnum(it.netCashFlow), cnum(it.endBalance))
        })
    println("Report 4 justification: shows deposits, withdrawals, net cash flow and total funds held per month,")
    println("so management can spot withdrawal spikes and liquidity risk across the whole bank.")
    println()

    writeSummary(txns, byAcc, refDate, r1, r2, r3)
}

fun main(args: Array<String>) {
    if (args.isNotEmpty() && args[0].equals("reports", ignoreCase = true)) {
        val res = ingest(CSV_FILE)
        if (res.txns.isEmpty()) println("No transactions loaded. Cannot generate reports.")
        else generateReports(res.txns)
        return
    }

    // variable declarations
    var choiceM: Int? // main menu selection
    var Echoice: String? // Y/N selection
    var ExChoice: Int? // exchange currency choice
    var choiceER: Int? // exchange rate choice
    var Schoice: Int? // source exchange rate choice
    var sAmount: Double? // source amount to be exchanged
    var running = true // program loop
    var nDays: Int? // days for interest
    var loaded: List<Txn> = emptyList()

    // exchange rate storage, index 1..6 = PHP, USD, JPY, GBP, EUR, CNY
    val eRate = doubleArrayOf(0.0, 1.00, 62.00, 0.40, 84.00, 72.00, 9.00)

    val currencyMenu = "[1] Philippine Peso (PHP)\n[2] United States Dollar (USD)\n[3] Japanese Yen (JPY)\n" +
            "[4] British Pound Sterling (GBP)\n[5] Euro (EUR)\n[6] Chinese Yuan Renminbi (CNY)\n"

    while (running) {
        // Main Menu
        println("Select Transaction:")
        println(
            "[1] Register Account\n[2] Deposit Amount\n[3] Withdraw Amount\n[4] Currency Exchange\n" +
                    "[5] Record Exchange Rates\n[6] Show Interest Amount\n[7] Generate Reports"
        )
        print("Choice: ")
        choiceM = readln().trim().toIntOrNull()
        println()

        var askBack = true

        when (choiceM) {
            1 -> {
                do {
                    registerAccount()
                } while (askAnother("Register another account?"))
                println()
                askBack = false
            }

            2, 3 -> {
                val isDeposit = choiceM == 2
                do {
                    transact(isDeposit)
                } while (askAnother(if (isDeposit) "Deposit another amount?" else "Withdraw another amount?"))
                println()
                askBack = false
            }

            4 -> {
                // Currency Exchange
                println("Foreign Currency Exchange\nSource Currency Option:\n$currencyMenu")
                println("Select Source Currency: ")
                Schoice = readln().trim().toIntOrNull()
                println("Source Amount: ")
                sAmount = readAmount()
                println("Exchange Currency Option:\n$currencyMenu")
                println("Exchange Currency:")
                ExChoice = readln().trim().toIntOrNull()

                if (Schoice == null || Schoice !in 1..6 || ExChoice == null || ExChoice !in 1..6 ||
                    sAmount == null || sAmount < 0.0) {
                    println("Invalid input.")
                } else {
                    val exchanged = sAmount * eRate[Schoice] / eRate[ExChoice]
                    println("Exchange Amount: ${money(exchanged)}")
                }
            }

            5 -> {
                // Record Exchange Rate
                println("Record Exchange Rates\n\n$currencyMenu")
                println("Select Exchange Rate: ")
                choiceER = readln().trim().toIntOrNull()
                if (choiceER == null || choiceER !in 1..6) {
                    println("Invalid choice.")
                } else {
                    println("Exchange Rate (PHP per 1 unit): ")
                    val r = readln().trim().toDoubleOrNull()
                    if (r == null || r.isNaN() || r.isInfinite() || r <= 0.0) println("Invalid rate.")
                    else { eRate[choiceER] = r; println("Recorded.") }
                }
            }

            6 -> {
                // Show Interest Amount
                val txns = ingest(CSV_FILE).txns
                println("Show Interest Amount")
                println("Account Number: ")
                val acc = readln().trim().toLongOrNull()
                val last = if (acc == null) null else findLatest(txns, acc)
                if (last == null) {
                    println("Account not found.")
                } else {
                    println("Account Name: ${last.name}\n")
                    println("Current Balance: ${money(last.balance)}\nCurrency: PHP\nInterest Rate: 5%")
                    println("Total Number of Days: ")
                    nDays = readln().trim().toIntOrNull()
                    if (nDays == null || nDays < 1) {
                        println("Invalid number of days.")
                    } else {
                        var projected = last.balance
                        println("Day | Interest | Balance |")
                        for (i in 1..nDays) {
                            val interest = projected * 0.05 / 365
                            projected = projected + interest
                            println("$i | ${money(interest)} | ${money(projected)}")
                        }
                    }
                }
            }

            7 -> {
                loaded = emptyList()
                var done = false
                while (!done) {
                    println("Generate Reports")
                    println()
                    println("[1] Load the file")
                    println("[2] Generate Reports")
                    print("Enter choice: ")
                    val rc = readln().trim().toIntOrNull()
                    println()
                    when (rc) {
                        1 -> {
                            val res = ingest(CSV_FILE)
                            loaded = res.txns
                            printLoadSummary(res)
                            println()
                        }
                        2 -> {
                            if (loaded.isEmpty()) {
                                println("No transactions loaded. Choose [1] Load the file first.")
                            } else {
                                generateReports(loaded)
                            }
                            done = true
                        }
                        else -> {
                            println("Invalid choice.")
                            println()
                        }
                    }
                }
            }

            0 -> {
                running = false
                askBack = false
            }

            else -> {
                println("Invalid choice. Please select 1-7.")
                println()
                askBack = false
            }
        }

        if (askBack) {
            println("\nBack to Main Menu (Y/N):")
            Echoice = readln()
            if (Echoice == "N" || Echoice == "n") running = false
            println()
        }
    }
}
