/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO1_9_view.h"
#include "MCO1_9_viewmodel.h"
#include <stdio.h>
#include <stdlib.h>

#define NAME_INPUT_SIZE 100

static void clear_input_buffer(void) {
    int c = getchar();
    while (c != '\n' && c != EOF) {
        c = getchar();
    }
}

int take_int_input(const char *prompt) {
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

int take_choice_input(const char *prompt, int min, int max) {
    int choice = take_int_input(prompt);

    while (choice < min || choice > max) {
        printf("Invalid choice. Please select a number from %d to %d.\n", min, max);
        choice = take_int_input(prompt);
    }
    return choice;
}

double take_double_input(const char *prompt) {
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

void take_string_input(char *buffer, const char *prompt) {
    printf("%s", prompt);
    if (scanf(" %99[^\n]", buffer) != 1) {
        exit(0);
    }
    clear_input_buffer();
}

bool take_yes_no_input(const char *prompt) {
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
    if (take_yes_no_input("Back to the Main Menu (Y/N): ")) {
        printf("\n");
    } else {
        ask_back_to_main_menu();
    }
}

void view_main_menu_navigation(void) {
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
        printf("[7] Exit\n\n");

        choice = take_int_input("Choice: ");

        switch (choice) {
            case 1:
                view_account_registration(&account);
                ask_back_to_main_menu();
                break;
            case 2:
                view_deposit_amount(&account);
                ask_back_to_main_menu();
                break;
            case 3:
                view_withdraw_amount(&account);
                ask_back_to_main_menu();
                break;
            case 4:
                view_foreign_currency_exchange();
                printf("\n");
                break;
            case 5:
                view_record_exchange_rate();
                ask_back_to_main_menu();
                break;
            case 6:
                view_show_interest_amount(&account);
                ask_back_to_main_menu();
                break;
            case 7:
                return;
            default:
                printf("Invalid choice. Please select a valid option.\n\n");
        }
    }
}

void view_account_registration(Account *account) {
    char account_name[NAME_INPUT_SIZE];
    int result;

    printf("\nRegister Account Name\n");
    take_string_input(account_name, "Account Name: ");

    result = viewmodel_register_account(account, account_name);
    if (result != RESULT_OK) {
        print_error(result);
    }
}

void view_deposit_amount(Account *account) {
    double amount;
    int result;

    printf("\nDeposit Amount\n");
    if (!is_account_registered(account)) {
        print_error(RESULT_NO_ACCOUNT);
        return;
    }
    print_account_details(account);

    amount = take_double_input("Deposit Amount: ");
    result = viewmodel_deposit(account, amount);
    if (result == RESULT_OK) {
        printf("Updated Balance: %.2f\n", account->balance);
    } else {
        print_error(result);
    }
}

void view_withdraw_amount(Account *account) {
    double amount;
    int result;

    printf("\nWithdraw Amount\n");
    if (!is_account_registered(account)) {
        print_error(RESULT_NO_ACCOUNT);
        return;
    }
    print_account_details(account);

    amount = take_double_input("Withdraw Amount: ");
    result = viewmodel_withdraw(account, amount);
    if (result == RESULT_OK) {
        printf("Updated Balance: %.2f\n", account->balance);
    } else {
        print_error(result);
    }
}

void view_foreign_currency_exchange(void) {
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

        source = take_choice_input("\nSource Currency: ", 1, CURRENCY_COUNT) - 1;
        source_amount = take_double_input("Source Amount: ");

        printf("\nExchanged Currency Options:\n");
        print_currency_options();

        target = take_choice_input("\nExchange Currency: ", 1, CURRENCY_COUNT) - 1;

        result = viewmodel_convert_currency(source, target, source_amount, &converted);
        if (result == RESULT_OK) {
            printf("Exchange Amount: %.2f\n", converted);
        } else {
            print_error(result);
        }

        printf("\n");
        again = take_yes_no_input("Convert another currency (Y/N)? ");
    }
}

void view_record_exchange_rate(void) {
    int currency;
    double rate;
    int result;

    printf("\nRecord Exchange Rate\n\n");
    print_currency_options();

    currency = take_choice_input("\nSelect Foreign Currency: ", 1, CURRENCY_COUNT) - 1;
    if (currency == PHP) {
        printf("PHP is the base currency. Its exchange rate is fixed at 1.00.\n");
        return;
    }

    rate = take_double_input("Exchange Rate: ");
    result = viewmodel_record_exchange_rate(currency, rate);
    if (result == RESULT_OK) {
        printf("Recorded: 1 %s = %.2f PHP\n", get_currency_code(currency), rate);
    } else {
        print_error(result);
    }
}

void view_show_interest_amount(Account *account) {
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

    days = take_int_input("Total Number of Days: ");
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