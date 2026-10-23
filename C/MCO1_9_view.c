/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO1_9_view.h"
#include "MCO1_9_viewmodel.h"
#include <ctype.h>
#include <errno.h>
#include <limits.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define INPUT_BUFFER_SIZE 100


void read_line(char *buffer, int buffer_size) {
    if (fgets(buffer, buffer_size, stdin) == NULL) {
        printf("\n");
        exit(0);
    }

    size_t length = strlen(buffer);
    if (length > 0 && buffer[length - 1] == '\n') {
        buffer[length - 1] = '\0';
    } else {
        int c;
        while ((c = getchar()) != '\n' && c != EOF) { }
    }
}

int take_int_input(const char *prompt) {
    char line[INPUT_BUFFER_SIZE];
    char *end;

    while (true) {
        printf("%s", prompt);
        read_line(line, sizeof(line));

        errno = 0;
        long value = strtol(line, &end, 10);
        while (isspace((unsigned char)*end)) end++;

        if (end != line && *end == '\0' && errno == 0 &&
            value >= INT_MIN && value <= INT_MAX) {
            return (int)value;
        }
        printf("Invalid input. Please enter a whole number.\n");
    }
}

int take_choice_input(const char *prompt, int min, int max) {
    while (true) {
        int choice = take_int_input(prompt);
        if (choice >= min && choice <= max) {
            return choice;
        }
        printf("Invalid choice. Please select a number from %d to %d.\n", min, max);
    }
}

double take_double_input(const char *prompt) {
    char line[INPUT_BUFFER_SIZE];
    char *end;

    while (true) {
        printf("%s", prompt);
        read_line(line, sizeof(line));

        errno = 0;
        double value = strtod(line, &end);
        while (isspace((unsigned char)*end)) end++;

        if (end != line && *end == '\0' && errno == 0 && isfinite(value)) {
            return value;
        }
        printf("Invalid input. Please enter a number.\n");
    }
}

void take_string_input(char *buffer, int buffer_size, const char *prompt) {
    while (true) {
        printf("%s", prompt);
        read_line(buffer, buffer_size);
        if (buffer[0] != '\0') {
            return;
        }
        printf("Input cannot be empty.\n");
    }
}

bool take_yes_no_input(const char *prompt) {
    char line[INPUT_BUFFER_SIZE];

    while (true) {
        printf("%s", prompt);
        read_line(line, sizeof(line));
        if (line[0] != '\0' && line[1] == '\0') {
            if (line[0] == 'Y' || line[0] == 'y') return true;
            if (line[0] == 'N' || line[0] == 'n') return false;
        }
        printf("Please enter Y or N.\n");
    }
}

static const char *get_result_message(Result result) {
    switch (result) {
        case OK:
            return "Success.";
        case INVALID_ARGUMENT:
            return "Invalid value. Amount/rate must be greater than zero.";
        case NAME_TOO_LONG:
            return "Account name cannot exceed 31 characters.";
        case NO_ACCOUNT:
            return "No account registered. Please register an account name first.";
        case INSUFFICIENT_FUNDS:
            return "Insufficient balance.";
    }
    return "Unknown error.";
}

static void print_error(Result result) {
    printf("Error: %s\n", get_result_message(result));
}

static void print_currency_options(void) {
    for (int i = PHP; i < CURRENCY_COUNT; i++) {
        printf("[%d] %s (%s)\n", i + 1, get_currency_name((Currency)i), get_currency_code((Currency)i));
    }
}

static void print_account_header(const Account *account) {
    printf("Account Name: %s\n", account->account_name);
    printf("Current Balance: %.2f\n", account->balance);
    printf("Currency: %s\n\n", get_currency_code(account->currency));
}


static void run_transaction(void (*screen)(Account *), Account *account) {
    do {
        screen(account);
        printf("\n");
    } while (!take_yes_no_input("Back to the Main Menu (Y/N): "));
    printf("\n");
}

static void run_transaction_without_account(void (*screen)(void)) {
    do {
        screen();
        printf("\n");
    } while (!take_yes_no_input("Back to the Main Menu (Y/N): "));
    printf("\n");
}

static void print_main_menu(void) {
    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n");
    printf("[7] Exit\n\n");
}

void view_main_menu_navigation(void) {
    Account account;
    viewmodel_init(&account);

    while (true) {
        print_main_menu();

        switch (take_int_input("Choice: ")) {
            case 1:
                run_transaction(view_account_registration, &account);
                break;
            case 2:
                run_transaction(view_deposit_amount, &account);
                break;
            case 3:
                run_transaction(view_withdraw_amount, &account);
                break;
            case 4:
                view_foreign_currency_exchange();
                printf("\n");
                break;
            case 5:
                run_transaction_without_account(view_record_exchange_rate);
                break;
            case 6:
                run_transaction(view_show_interest_amount, &account);
                break;
            case 7:
                return;
            default:
                printf("Invalid choice. Please select a valid option.\n\n");
        }
    }
}

void view_account_registration(Account *account) {
    char account_name[INPUT_BUFFER_SIZE];

    printf("\nRegister Account Name\n");
    take_string_input(account_name, sizeof(account_name), "Account Name: ");

    Result result = viewmodel_register_account(account, account_name);
    if (result != OK) {
        print_error(result);
    }
}

void view_deposit_amount(Account *account) {
    printf("\nDeposit Amount\n");

    if (!is_account_registered(account)) {
        print_error(NO_ACCOUNT);
        return;
    }
    print_account_header(account);

    double amount = take_double_input("Deposit Amount: ");
    Result result = viewmodel_deposit(account, amount);

    if (result == OK) {
        printf("Updated Balance: %.2f\n", account->balance);
    } else {
        print_error(result);
    }
}

void view_withdraw_amount(Account *account) {
    printf("\nWithdraw Amount\n");

    if (!is_account_registered(account)) {
        print_error(NO_ACCOUNT);
        return;
    }
    print_account_header(account);

    double amount = take_double_input("Withdraw Amount: ");
    Result result = viewmodel_withdraw(account, amount);

    if (result == OK) {
        printf("Updated Balance: %.2f\n", account->balance);
    } else {
        print_error(result);
    }
}

void view_foreign_currency_exchange(void) {
    do {
        printf("\nForeign Currency Exchange\n");
        printf("Source Currency Option:\n");
        print_currency_options();

        Currency source = (Currency)(take_choice_input("\nSource Currency: ", 1, CURRENCY_COUNT) - 1);
        double source_amount = take_double_input("Source Amount: ");

        printf("\nExchanged Currency Options:\n");
        print_currency_options();

        Currency target = (Currency)(take_choice_input("\nExchange Currency: ", 1, CURRENCY_COUNT) - 1);

        double converted = 0.0;
        Result result = viewmodel_convert_currency(source, target, source_amount, &converted);
        if (result == OK) {
            printf("Exchange Amount: %.2f\n", converted);
        } else {
            print_error(result);
        }
        printf("\n");
    } while (take_yes_no_input("Convert another currency (Y/N)? "));
}

void view_record_exchange_rate(void) {
    printf("\nRecord Exchange Rate\n\n");
    print_currency_options();

    Currency currency = (Currency)(take_choice_input("\nSelect Foreign Currency: ", 1, CURRENCY_COUNT) - 1);

    if (currency == PHP) {
        printf("PHP is the base currency; its exchange rate is fixed at 1.00.\n");
        return;
    }

    double rate = take_double_input("Exchange Rate: ");
    Result result = viewmodel_record_exchange_rate(currency, rate);

    if (result == OK) {
        printf("Recorded: 1 %s = %.2f PHP\n", get_currency_code(currency), rate);
    } else {
        print_error(result);
    }
}

void view_show_interest_amount(Account *account) {
    printf("\nShow Interest Amount\n");

    if (!is_account_registered(account)) {
        print_error(NO_ACCOUNT);
        return;
    }
    print_account_header(account);
    printf("Interest Rate: %.0f%%\n\n", ANNUAL_INTEREST_RATE * 100);

    int days = take_int_input("Total Number of Days: ");
    if (days <= 0) {
        printf("Error: Number of days must be greater than zero.\n");
        return;
    }

    printf("\nDay | Interest | Balance |\n");
    double balance = account->balance;
    for (int day = 1; day <= days; day++) {
        double interest = viewmodel_daily_interest(balance);
        balance += interest;
        printf("%-3d | %-8.2f | %-7.2f |\n", day, interest, balance);
    }
}