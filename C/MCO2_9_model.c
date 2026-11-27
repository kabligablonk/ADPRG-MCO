/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO2_9_model.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

/* Index order follows the Currency enum: PHP, USD, JPY, GBP, EUR, CNY */
static double exchange_rates[CURRENCY_COUNT] = {1.00, 62.00, 0.40, 84.00, 72.00, 9.00};

static const char *currency_codes[CURRENCY_COUNT] = {
    "PHP", "USD", "JPY", "GBP", "EUR", "CNY"
};

static const char *transaction_types[TRANSACTION_TYPE_COUNT] = {
    "Deposit", "Withdraw"
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
    account->account_number = 0;

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


double daily_interest_calculate(double balance) {
    double interest = balance * (ANNUAL_INTEREST_RATE / 365);
    long centavos = (long)(interest * 100 + 0.5);
    return centavos / 100.0;
}


void csv_init(const char *filename) {
    FILE *file = fopen(filename, "r");
    if (file) { fclose(file); return; }
    file = fopen(filename, "w");
    if (file) {
        fprintf(file, "account_name,account_number,date,time,currency,amount,type,current_balance\n");
        fclose(file);
    }
}

unsigned long long get_next_account_number(const char *filename) {
    FILE *file = fopen(filename, "r");
    char line[256];
    char *comma;
    unsigned long long value, highest = 0;

    if (!file) return 1000000001ULL;
    while (fgets(line, sizeof(line), file)) {
        comma = strchr(line, ',');
        if (!comma) continue;
        value = strtoull(comma + 1, NULL, 10);
        if (value > highest) highest = value;
    }
    fclose(file);
    return highest == 0 ? 1000000001ULL : highest + 1;
}

void write_to_csv(const char *filename, const Account *account,
                  const char *transaction_type, double amount) {
    FILE *file;
    time_t now = time(NULL);
    struct tm *local = localtime(&now);
    char date[11];
    char time_value[9];

    strftime(date, sizeof(date), "%d/%m/%Y", local);
    strftime(time_value, sizeof(time_value), "%H:%M:%S", local);

    file = fopen(filename, "a");
    if (file) {
        fprintf(file, "%s,%llu,%s,%s,%s,%.2f,%s,%.2f\n",
                account->account_name, account->account_number, date, time_value,
                get_currency_code(account->currency), amount, transaction_type,
                account->balance);
        fclose(file);
    }
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