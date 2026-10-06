/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO1_BasicIO_9_C.h"
#include <stdio.h>
#include <stdbool.h>

static double usd_to_php = 62.00;
static double jpy_to_php = 0.40;
static double gbp_to_php = 84.00;
static double eur_to_php = 72.00;
static double cny_to_php = 9.00;

int main(void) {
    view_main_menu_navigation();
    view_account_registration();
    view_deposit_amount();
    view_withdraw_amount();
    view_record_exchange_rate();
    view_foreign_currency_exchange();
    return 0;
}

int take_int_input(const char *prompt, bool show_confirmation) {
    int input = 0;
    printf("%s", prompt);
    scanf("%d", &input);
    if (show_confirmation) {
        printf("\n***\n");
        printf("Choice = %d\n", input);
    }
    return input;
}

void take_string_input(char *buffer, int buffer_size, const char *prompt, bool show_confirmation) {
    printf("%s", prompt);
    scanf(" %99[^\n]", buffer);
    if (show_confirmation) {
        printf("\n***\n");
        printf("Account Name = %s\n", buffer);
    }
}

double take_double_input(const char *prompt, bool show_confirmation) {
    double input = 0.0;
    printf("%s", prompt);
    scanf("%lf", &input);
    if (show_confirmation) {
        printf("\n***\n");
        printf("Exchange Rate = %.2f\n", input);
    }
    return input;
}

void view_main_menu_navigation(void) {
    printf("Select Transaction:\n");
    printf("[1] Register Account Name\n");
    printf("[2] Deposit Amount\n");
    printf("[3] Withdraw Amount\n");
    printf("[4] Currency Exchange\n");
    printf("[5] Record Exchange Rates\n");
    printf("[6] Show Interest Amount\n\n");

    take_int_input("Choice: ", true);
}

void view_account_registration(void) {
    char account_name[100];

    printf("\nRegister Account Name\n");
    take_string_input(account_name, sizeof(account_name), "Account Name: ", true);
}

void view_deposit_amount(void) {
    char account_name[100];
    const char currency[] = "PHP";
    double balance = 1000.00;
    double deposit_amount = 0.0;

    printf("\nDeposit Amount\n");
    take_string_input(account_name, sizeof(account_name), "Account Name: ", false);
    printf("Current Balance: %.2f\n", balance);
    printf("Currency: %s\n\n", currency);

    deposit_amount = take_double_input("Deposit Amount: ", false);

    printf("\n***\n");
    printf("Account Name = %s\n", account_name);
    printf("Deposit Amount = %.2f\n", deposit_amount);
}

void view_withdraw_amount(void) {
    char account_name[100];
    const char currency[] = "PHP";
    double balance = 1000.00;
    double withdraw_amount = 0.0;

    printf("\nWithdraw Amount\n");
    take_string_input(account_name, sizeof(account_name), "Account Name: ", false);
    printf("Current Balance: %.2f\n", balance);
    printf("Currency: %s\n\n", currency);

    withdraw_amount = take_double_input("Withdraw Amount: ", false);

    printf("\n***\n");
    printf("Account Name = %s\n", account_name);
    printf("Withdraw Amount = %.2f\n", withdraw_amount);
}

void view_record_exchange_rate(void) {
    int choice = 0;
    double exch_rate = 0.0;

    printf("\nRecord Exchange Rate\n\n");
    printf("[1] Philippine Peso (PHP)\n");
    printf("[2] United States Dollar (USD)\n");
    printf("[3] Japanese Yen (JPY)\n");
    printf("[4] British Pound Sterling (GBP)\n");
    printf("[5] Euro (EUR)\n");
    printf("[6] Chinese Yuan Renminni (CNY)\n\n");

    printf("Select Foreign Currency: ");
    scanf("%d", &choice);

    exch_rate = take_double_input("Exchange Rate: ", false);

    printf("\n***\n");
    printf("Select Foreign Currency = [%d]\n", choice);
    printf("Exchange Rate = %.2f\n", exch_rate);
}

void view_foreign_currency_exchange(void) {
    double source_amount = 0.0;

    printf("\nForeign Currency Exchange\n");
    printf("Source Amount (PHP): ");
    scanf("%lf", &source_amount);

    printf("\nExchanged Currency\n");
    printf("[1] Philippine Peso (PHP) = %.2f\n", source_amount);
    printf("[2] United States Dollar (USD) = %.2f\n", source_amount * usd_to_php);
    printf("[3] Japanese Yen (JPY) = %.2f\n", source_amount * jpy_to_php);
    printf("[4] British Pound Sterling (GBP) = %.2f\n", source_amount * gbp_to_php);
    printf("[5] Euro (EUR) = %.2f\n", source_amount * eur_to_php);
    printf("[6] Chinese Yuan Renminni (CNY) = %.2f\n", source_amount * cny_to_php);

    printf("\n***\n");
    printf("Source Currency = Philippine Peso (PHP)\n");
    printf("Source Amount (PHP) = %.2f\n", source_amount);
}