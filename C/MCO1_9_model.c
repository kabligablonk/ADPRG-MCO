/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO1_9_model.h"
#include <math.h>
#include <string.h>

static double exchange_rates[CURRENCY_COUNT] = {
    1.00,   // PHP
    62.00,  // USD
    0.40,   // JPY
    84.00,  // GBP
    72.00,  // EUR
    9.00    // CNY
};

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
    return currency >= PHP && currency < CURRENCY_COUNT;
}


void init_account(Account *account) {
    account->account_name[0] = '\0';
    account->balance = 0.0;
    account->currency = PHP;
}

Result set_account_name(Account *account, const char *name) {
    if (account == NULL || name == NULL || name[0] == '\0') {
        return INVALID_ARGUMENT;
    }
    if (strlen(name) >= ACCOUNT_NAME_SIZE) {
        return NAME_TOO_LONG;
    }
    strcpy(account->account_name, name);
    return OK;
}

int is_account_registered(const Account *account) {
    return account != NULL && account->account_name[0] != '\0';
}

Result deposit_account(Account *account, double amount) {
    if (account == NULL || amount <= 0) {
        return INVALID_ARGUMENT;
    }
    account->balance += amount;
    return OK;
}

Result withdraw_account(Account *account, double amount) {
    if (account == NULL || amount <= 0) {
        return INVALID_ARGUMENT;
    }
    if (amount > account->balance) {
        return INSUFFICIENT_FUNDS;
    }
    account->balance -= amount;
    return OK;
}


void init_exchange_rates(void) {
    for (int i = 0; i < CURRENCY_COUNT; i++) {
        exchange_rates[i] = 0.0;
    }
}

Result set_exchange_rate(Currency currency, double rate) {
    if (!is_valid_currency(currency) || currency == PHP || rate <= 0) {
        return INVALID_ARGUMENT;
    }
    exchange_rates[currency] = rate;
    return OK;
}

double get_exchange_rate(Currency currency) {
    return is_valid_currency(currency) ? exchange_rates[currency] : 0.0;
}

Result convert_currency(Currency from, Currency to, double amount, double *converted) {
    if (!is_valid_currency(from) || !is_valid_currency(to) ||
        amount < 0 || converted == NULL) {
        return INVALID_ARGUMENT;
    }
    /* Everything is routed through the PHP base currency. */
    *converted = amount * exchange_rates[from] / exchange_rates[to];
    return OK;
}

double compute_daily_interest(double balance) {
    double interest = balance * (ANNUAL_INTEREST_RATE / 365.0);
    return floor(interest * 100.0 + 0.5) / 100.0;   /* credited in whole centavos */
}

const char *get_currency_code(Currency currency) {
    return is_valid_currency(currency) ? currency_codes[currency] : "???";
}

const char *get_currency_name(Currency currency) {
    return is_valid_currency(currency) ? currency_names[currency] : "Unknown";
}