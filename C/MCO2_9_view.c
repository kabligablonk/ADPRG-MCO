/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO2_9_view.h"
#include "MCO2_9_viewmodel.h"
#include <stdio.h>
#include <stdlib.h>

#define NAME_INPUT_SIZE 100

static void clear_input_buffer(void) {
    int c = getchar();
    while (c != '\n' && c != EOF) {
        c = getchar();
    }
}

int prompt_for_integer(const char *prompt) {
    int value = 0;
    int status;

    printf("%s", prompt);
    status = scanf("%d", &value);
    while (status != 1) {
        if (status == EOF) {
            exit(0);
        }
        clear_input_buffer();
        printf("Invalid input. Please enter a whole number.\n");
        printf("%s", prompt);
        status = scanf("%d", &value);
    }
    clear_input_buffer();
    return value;
}

int prompt_for_bounded_choice(const char *prompt, int min, int max) {
    int choice = prompt_for_integer(prompt);

    while (choice < min || choice > max) {
        printf("Invalid choice. Please select a number from %d to %d.\n", min, max);
        choice = prompt_for_integer(prompt);
    }
    return choice;
}

double prompt_for_double(const char *prompt) {
    double value = 0.0;
    int status;

    printf("%s", prompt);
    status = scanf("%lf", &value);
    while (status != 1) {
        if (status == EOF) {
            exit(0);
        }
        clear_input_buffer();
        printf("Invalid input. Please enter a number.\n");
        printf("%s", prompt);
        status = scanf("%lf", &value);
    }
    clear_input_buffer();
    return value;
}

void prompt_for_string(char *buffer, const char *prompt) {
    printf("%s", prompt);
    if (scanf(" %99[^\n]", buffer) != 1) {
        exit(0);
    }
    clear_input_buffer();
}

bool prompt_for_yes_no(const char *prompt) {
    char choice = ' ';

    printf("%s", prompt);
    if (scanf(" %c", &choice) != 1) {
        exit(0);
    }
    clear_input_buffer();

    while (choice != 'Y' && choice != 'y' && choice != 'N' && choice != 'n') {
        printf("Please enter Y or N.\n");
        printf("%s", prompt);
        if (scanf(" %c", &choice) != 1) {
            exit(0);
        }
        clear_input_buffer();
    }
    return choice == 'Y' || choice == 'y';
}


static void print_error(int result) {
    printf("Error: ");
    if (result == RESULT_INVALID) {
        printf("Invalid value. It must be greater than zero.\n");
    } else if (result == RESULT_NAME_TOO_LONG) {
        printf("Account name cannot exceed 31 characters.\n");
    } else if (result == RESULT_NO_ACCOUNT) {
        printf("No account registered. Please register an account name first.\n");
    } else if (result == RESULT_BELOW_MINIMUM) {
        printf("Initial deposit must be at least Php 5,000.00.\n");
    } else if (result == RESULT_INSUFFICIENT_FUNDS) {
        printf("Insufficient balance.\n");
    } else {
        printf("Something went wrong.\n");
    }
}

static void print_currency_options(void) {
    int i;

    for (i = 0; i < CURRENCY_COUNT; i++) {
        printf("[%d] %s (%s)\n", i + 1, get_currency_name(i), get_currency_code(i));
    }
}

static void print_account_details(const Account *account) {
    printf("Account Name: %s\n", account->account_name);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", get_currency_code(account->currency));
}

static void ask_back_to_main_menu(void) {
    printf("\n");
    if (prompt_for_yes_no("Back to the Main Menu (Y/N): ")) {
        printf("\n");
    } else {
        ask_back_to_main_menu();
    }
}

void run_main_menu(void) {
    Account account;
    int choice;

    viewmodel_init(&account);

    while (true) {
        printf("Select Transaction:\n");
        printf("[1] Register Account Name\n");
        printf("[2] Deposit Amount\n");
        printf("[3] Withdraw Amount\n");
        printf("[4] Currency Exchange\n");
        printf("[5] Record Exchange Rates\n");
        printf("[6] Show Interest Amount\n");
        printf("[7] Generate Reports\n\n");
        printf("[8] Exit\n");

        choice = prompt_for_integer("Choice: ");

        switch (choice) {
            case 1:
                handle_account_registration(&account);
                ask_back_to_main_menu();
                break;
            case 2:
                handle_deposit(&account);
                ask_back_to_main_menu();
                break;
            case 3:
                handle_withdrawal(&account);
                ask_back_to_main_menu();
                break;
            case 4:
                handle_currency_exchange();
                printf("\n");
                break;
            case 5:
                handle_exchange_rate_update();
                ask_back_to_main_menu();
                break;
            case 6:
                display_interest_projection(&account);
                ask_back_to_main_menu();
                break;
            case 7:
                run_report_menu();
                printf("\n");
                break;
            case 8:
                return;
            default:
                printf("Invalid choice. Please select a valid option.\n\n");
        }
    }
}

void handle_account_registration(Account *account) {
    char account_name[NAME_INPUT_SIZE];
    int result;

    double initial_deposit;

    printf("\nRegister Account\n");
    prompt_for_string(account_name, "Account Name: ");
    initial_deposit = prompt_for_double("Initial Deposit: ");

    result = viewmodel_register_account(account, account_name, initial_deposit);
    if (result == RESULT_OK) {
        printf("\nAccount created successfully!\n");
    } else {
        print_error(result);
    }
}

void handle_deposit(Account *account) {
    double amount;
    int result;

    printf("\nDeposit Amount\n");
    if (!is_account_registered(account)) {
        print_error(RESULT_NO_ACCOUNT);
        return;
    }
    print_account_details(account);

    amount = prompt_for_double("Deposit Amount: ");
    result = viewmodel_deposit(account, amount);
    if (result == RESULT_OK) {
        printf("Updated Balance: %.2f\n", account->balance);
    } else {
        print_error(result);
    }
}

void handle_withdrawal(Account *account) {
    double amount;
    int result;

    printf("\nWithdraw Amount\n");
    if (!is_account_registered(account)) {
        print_error(RESULT_NO_ACCOUNT);
        return;
    }
    print_account_details(account);

    amount = prompt_for_double("Withdraw Amount: ");
    result = viewmodel_withdraw(account, amount);
    if (result == RESULT_OK) {
        printf("Updated Balance: %.2f\n", account->balance);
    } else {
        print_error(result);
    }
}

void handle_currency_exchange(void) {
    int source;
    int target;
    double source_amount;
    double converted;
    int result;
    bool again = true;

    while (again) {
        printf("\nForeign Currency Exchange\n");
        printf("Source Currency Option:\n");
        print_currency_options();

        source = prompt_for_bounded_choice("\nSource Currency: ", 1, CURRENCY_COUNT) - 1;
        source_amount = prompt_for_double("Source Amount: ");

        printf("\nExchanged Currency Options:\n");
        print_currency_options();

        target = prompt_for_bounded_choice("\nExchange Currency: ", 1, CURRENCY_COUNT) - 1;

        result = viewmodel_convert_currency(source, target, source_amount, &converted);
        if (result == RESULT_OK) {
            printf("Exchange Amount: %.2f\n", converted);
        } else {
            print_error(result);
        }

        printf("\n");
        again = prompt_for_yes_no("Convert another currency (Y/N)? ");
    }
}

void handle_exchange_rate_update(void) {
    int currency;
    double rate;
    int result;

    printf("\nRecord Exchange Rate\n\n");
    print_currency_options();

    currency = prompt_for_bounded_choice("\nSelect Foreign Currency: ", 1, CURRENCY_COUNT) - 1;
    if (currency == PHP) {
        printf("PHP is the base currency. Its exchange rate is fixed at 1.00.\n");
        return;
    }

    rate = prompt_for_double("Exchange Rate: ");
    result = viewmodel_record_exchange_rate(currency, rate);
    if (result == RESULT_OK) {
        printf("Recorded: 1 %s = %.2f PHP\n", get_currency_code(currency), rate);
    } else {
        print_error(result);
    }
}

void display_interest_projection(Account *account) {
    int days;
    int day;
    double interest;
    double balance;

    printf("\nShow Interest Amount\n");
    if (!is_account_registered(account)) {
        print_error(RESULT_NO_ACCOUNT);
        return;
    }
    print_account_details(account);
    printf("Interest Rate: %.0f%%\n\n", ANNUAL_INTEREST_RATE * 100);

    days = prompt_for_integer("Total Number of Days: ");
    if (days <= 0) {
        print_error(RESULT_INVALID);
        return;
    }

    printf("\nDay | Interest | Balance |\n");
    balance = account->balance;
    for (day = 1; day <= days; day++) {
        interest = viewmodel_daily_interest(balance);
        balance += interest;
        printf("%-3d | %-8.2f | %-7.2f |\n", day, interest, balance);
    }
}

void print_report_previews(const ReportSet *set) {
    size_t i;

    printf("\nReport 1: Banking Privilege Tiers\n");
    printf("%-20s | %-10s | %-15s | %-20s | %s\n",
           "AccountName", "AccountAge", "CurrentBalance", "AverageDailyBalance", "PrivilegeTier");
    for (i = 0; i < set->n1 && i < 2; i++) {
        const Report1Row *r = &set->r1[i];
        printf("%-20s | %-10ld | %-15.2f | %-20.2f | %s\n", r->name, r->account_age,
               r->current_balance, r->average_daily_balance, r->tier);
    }
    printf("(Full table exported to %s)\n", REPORT1_FILE);

    printf("\nReport 2: Interest Expense Projection\n");
    printf("%-20s | %-15s | %s\n", "AccountName", "CurrentBalance", "ExpectedInterest");
    for (i = 0; i < set->n2 && i < 2; i++) {
        const Report2Row *r = &set->r2[i];
        printf("%-20s | %-15.2f | %.2f\n", r->name, r->current_balance, r->expected_interest);
    }
    printf("(Full table exported to %s)\n", REPORT2_FILE);

    printf("\nReport 3: Suspicious Client Behavior\n");
    printf("%-20s | %-10s | %-12s | %-8s | %-12s | %-8s | %s\n", "AccountName", "Date",
           "Withdrawals", "Deposits", "DepositTotal", "Velocity", "RiskFlag");
    for (i = 0; i < set->n3 && i < 2; i++) {
        const Report3Row *r = &set->r3[i];
        printf("%-20s | %02d/%02d/%04d | %-12.2f | %-8d | %-12.2f | ", r->name, r->day,
               r->month, r->year, r->total_withdrawals, r->deposit_count, r->total_deposits);
        if (r->has_turnover) printf("%.2f%% | ", r->turnover_velocity);
        else printf("N/A | ");
        printf("%s\n", r->suspicious ? "Suspicious" : "Normal");
    }
    printf("(Full table exported to %s)\n", REPORT3_FILE);

    printf("\nReport 4: Client Loyalty Score\n");
    printf("%-20s | %-10s | %-15s | %-10s | %s\n",
           "AccountName", "AccountAge", "CurrentBalance", "RawIndex", "LoyaltyScore");
    for (i = 0; i < set->n4 && i < 2; i++) {
        const Report4Row *r = &set->r4[i];
        printf("%-20s | %-10ld | %-15.2f | %-10.2f | %.2f\n", r->name, r->account_age,
               r->current_balance, r->raw_index, r->loyalty_score);
    }
    printf("(Full table exported to %s)\n", REPORT4_FILE);

    printf("\nSummary State (%s):\n", SUMMARY_FILE);
    printf("{\"total_accounts\": %zu, \"total_deposits\": %.2f, \"total_withdrawals\": %.2f, "
           "\"total_balance\": %.2f, \"total_interest_expense\": %.2f}\n",
           set->summary.total_accounts, set->summary.total_deposits,
           set->summary.total_withdrawals, set->summary.total_balance,
           set->summary.total_interest_expense);
}

void run_report_menu(void) {
    static bool loaded = false;
    LoadStats stats;
    ReportSet set;
    int choice;

    do {
        printf("\nGenerate Reports\n\n");
        printf("[1] Load the file\n[2] Generate Reports\n[3] Back\n");
        choice = prompt_for_bounded_choice("Enter choice: ", 1, 3);

        if (choice == 1) {
            printf("\nProcessing dataset...\n");
            if (viewmodel_load_dataset(&stats) != RESULT_OK) {
                printf("Error: could not read %s.\n", CSV_FILE);
                loaded = false;
            } else {
                printf("(%zu rows loaded, %zu filtered for PHP currency, %zu invalid rows skipped)\n",
                       stats.rows_read, stats.rows_php, stats.rows_invalid);
                loaded = true;
            }
        } else if (choice == 2) {
            if (!loaded) {
                printf("\nPlease load the file first (option 1).\n");
            } else if (viewmodel_generate_reports(&set) != RESULT_OK) {
                printf("\nError: could not generate the reports.\n");
            } else {
                printf("\nGenerating reports...\nOutputs saved to individual files...\n");
                print_report_previews(&set);
                report_set_free(&set);
            }
        }
    } while (choice != 3);
}