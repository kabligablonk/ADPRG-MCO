/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "../C/MCO1_BasicIO_C.h"

#include <stdio.h>
#include <stdbool.h>

static double php = 1.00;
static double usd_to_php = 62.00;
static double jpy_to_php = 0.40;
static double gbp_to_php = 84.00;
static double eur_to_php = 72.00;
static double cny_to_php = 9.00;

void view_main_menu_navigation() {
    int choice = 0;

    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n");
    choice = take_int_input(choice, "choice:", true);

    switch (choice) {
        case 1:
            view_account_registration();
            break;
        case 2:
            view_deposit_amount();
            break;
        case 3:
            view_withdraw_amount();
            break;
        case 4:
            view_foreign_currency_exchange();
            break;
        case 5:
            view_record_exchange_rate();
            break;
        case 6:
            break;
        default:
            printf("Invalid choice. Please try again.\n");
    }
}

void view_account_registration() {
    char choice[] = "";

    printf("Register Account Name\n");
    take_string_input(choice, "Account Name:", true);
}

void view_deposit_amount() {
    char account_name[] = "";
    char currency[] = "PHP";
    int balance = 1000;
    int deposit_amount = 0;

    take_string_input(account_name, "Account Name:", false);
    printf("Current Balance: %d\n", balance);
    printf("Currency: %s\n", currency);
    deposit_amount = take_int_input(deposit_amount, "Deposit Amount:", false);

    printf("\n***\n");
    printf("Account Name: %s\n", account_name);
    printf("Deposit Amount: %d\n", deposit_amount);
}

void view_withdraw_amount() {
    char account_name[] = "";
    char currency[] = "PHP";
    int balance = 1000;
    int withdraw_amount = 0;

    take_string_input(account_name, "Account Name:", false);
    printf("Current Balance: %d\n", balance);
    printf("Currency: %s\n", currency);
    withdraw_amount = take_int_input(withdraw_amount, "Withdraw Amount:", false);

    printf("\n***\n");
    printf("Account Name: %s\n", account_name);
    printf("Withdraw Amount: %d\n", withdraw_amount);
}

void view_record_exchange_rate() {
    int choice = 0;

    printf("Record Exchange Rate\n");
    printf("[1] Philippine Peso (PHP)\n");
    printf("[2] United States Dollar (USD)\n");
    printf("[3] Japanese Yen (JPY)\n");
    printf("[4] British Pound Sterling (GBP)\n");
    printf("[5] Euro (EUR)\n");
    printf("[6] Chinese Yuan Renminni (CNY)\n");
    choice = take_int_input(choice, "Select Foreign Currency:", false);

    switch (choice) {
        case 1:
            php = take_double_input(php, "Enter Exchange Rate:", false);
            view_record_exchange_confirmation(choice, php);
            break;
        case 2:
            usd_to_php = take_double_input(usd_to_php, "Enter Exchange Rate:", false);
            view_record_exchange_confirmation(choice, usd_to_php);
            break;
        case 3:
            jpy_to_php = take_double_input(jpy_to_php, "Enter Exchange Rate:", false);
            view_record_exchange_confirmation(choice, jpy_to_php);
            break;
        case 4:
            gbp_to_php = take_double_input(gbp_to_php, "Enter Exchange Rate:", false);
            view_record_exchange_confirmation(choice, gbp_to_php);
            break;
        case 5:
            eur_to_php = take_double_input(eur_to_php, "Enter Exchange Rate:", false);
            view_record_exchange_confirmation(choice, eur_to_php);
            break;
        case 6:
            cny_to_php = take_double_input(cny_to_php, "Enter Exchange Rate:", false);
            view_record_exchange_confirmation(choice, cny_to_php);
            break;
        default:
            printf("Invalid choice. Please try again.\n");
    }
}

void view_foreign_currency_exchange() {
    double source_amount = 0;

    printf("Foreign Currency Exchange\n");
    source_amount = take_double_input(source_amount, "Source Amount (PHP):", false);

    printf("Exchanged Currency\n");
    printf("[1] Philippine Peso (PHP): %.2f\n", source_amount);
    printf("[2] United States Dollar (USD): %.2f\n", source_amount * usd_to_php);
    printf("[3] Japanese Yen (JPY): %.2f\n", source_amount * jpy_to_php);
    printf("[4] British Pound Sterling (GBP): %.2f\n", source_amount * gbp_to_php);
    printf("[5] Euro (EUR): %.2f\n", source_amount * eur_to_php);
    printf("[6] Chinese Yuan Renminni (CNY): %.2f\n", source_amount * cny_to_php);

    printf("\n***\n");
    printf("Source Currency = Philippine Peso (PHP)\n");
    printf("Source Amount: %.2f\n", source_amount);

}

int take_int_input(int input, char *prompt, bool show_confirmation) {
    printf("%s ", prompt);
    scanf("%d", &input);
    if (show_confirmation) {
        printf("\n***\n");
        printf("%s %d\n", prompt, input);
    }

    return input;
}

char take_string_input(char *input, char *prompt, bool show_confirmation) {
    printf("%s", prompt);
    scanf("%s", input);
    if (show_confirmation) {
        printf("\n***\n");
        printf("%s %s\n", prompt, input);
    }
    return *input;
}

double take_double_input(double input, char *prompt, bool show_confirmation) {
    printf("%s ", prompt);
    scanf("%lf", &input);
    if (show_confirmation) {
        printf("\n***\n");
        printf("%s %.2f\n", prompt, input);
    }

    return input;
}

void view_record_exchange_confirmation(int selected_currency, double exchange_rate) {
    printf("\n***\n");
    printf("Selected Currency: %d\n", selected_currency);
    printf("Exchange Rate: %.2f\n", exchange_rate);
}

int main(int argc, char *argv[]) {
    view_main_menu_navigation();
    return 0;
}
