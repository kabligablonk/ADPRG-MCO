/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO1_9_model.h"
#include <string.h>

/* Index order follows the Currency enum: PHP, USD, JPY, GBP, EUR, CNY */
static double exchange_rates[CURRENCY_COUNT] = {1.00, 62.00, 0.40, 84.00, 72.00, 9.00};

static const char *currency_codes[CURRENCY_COUNT] = {
    "PHP", "USD", "JPY", "GBP", "EUR", "CNY"
};

static const char *currency_names[CURRENCY_COUNT] = {
    "Philippine Peso",
    "United States Dollar",
    "Japanese Yen",
    "British Pound Sterling",
    "Euro",
    "Chinese Yuan Renminni"
};

static int is_valid_currency(Currency currency) {
    if (currency < 0 || currency >= CURRENCY_COUNT) {
        return 0;
    }
    return 1;
}

void init_account(Account *account) {
    account->account_name[0] = '\0';
    account->balance = 0.0;
    account->currency = PHP;
}

int set_account_name(Account *account, const char *name) {
    if (name[0] == '\0') {
        return RESULT_INVALID;
    }
    if (strlen(name) >= ACCOUNT_NAME_SIZE) {
        return RESULT_NAME_TOO_LONG;
    }
    strcpy(account->account_name, name);
    return RESULT_OK;
}

int is_account_registered(const Account *account) {
    if (account->account_name[0] == '\0') {
        return 0;
    }
    return 1;
}

int deposit_account(Account *account, double amount) {
    if (amount <= 0) {
        return RESULT_INVALID;
    }
    account->balance += amount;
    return RESULT_OK;
}

int withdraw_account(Account *account, double amount) {
    if (amount <= 0) {
        return RESULT_INVALID;
    }
    if (amount > account->balance) {
        return RESULT_INSUFFICIENT_FUNDS;
    }
    account->balance -= amount;
    return RESULT_OK;
}

int set_exchange_rate(Currency currency, double rate) {
    if (!is_valid_currency(currency) || currency == PHP || rate <= 0) {
        return RESULT_INVALID;
    }
    exchange_rates[currency] = rate;
    return RESULT_OK;
}

int convert_currency(Currency from, Currency to, double amount, double *converted) {
    if (!is_valid_currency(from) || !is_valid_currency(to) || amount < 0) {
        return RESULT_INVALID;
    }
    *converted = amount * exchange_rates[from] / exchange_rates[to];
    return RESULT_OK;
}


double compute_daily_interest(double balance) {
    double interest = balance * (ANNUAL_INTEREST_RATE / 365);
    long centavos = (long)(interest * 100 + 0.5);   /* round to 2 decimal places */
    return centavos / 100.0;
}


const char *get_currency_code(Currency currency) {
    if (!is_valid_currency(currency)) {
        return "???";
    }
    return currency_codes[currency];
}

const char *get_currency_name(Currency currency) {
    if (!is_valid_currency(currency)) {
        return "Unknown";
    }
    return currency_names[currency];
}