/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#ifndef ADPRG_MCO_MCO1_9_MODEL_H
#define ADPRG_MCO_MCO1_9_MODEL_H

#define ACCOUNT_NAME_SIZE   32
#define CURRENCY_COUNT      6
#define ANNUAL_INTEREST_RATE 0.05

typedef enum {
    PHP,
    USD,
    JPY,
    GBP,
    EUR,
    CNY
} Currency;

typedef enum {
    OK = 0,
    INVALID_ARGUMENT,
    NAME_TOO_LONG,
    NO_ACCOUNT,
    INSUFFICIENT_FUNDS
} Result;

typedef struct {
    char account_name[ACCOUNT_NAME_SIZE];
    double balance;
    Currency currency;
} Account;

void   init_account(Account *account);
Result set_account_name(Account *account, const char *name);
int    is_account_registered(const Account *account);
Result deposit_account(Account *account, double amount);
Result withdraw_account(Account *account, double amount);
void   init_exchange_rates(void);
Result set_exchange_rate(Currency currency, double rate);
double get_exchange_rate(Currency currency);
Result convert_currency(Currency from, Currency to, double amount, double *converted);
double compute_daily_interest(double balance);
const char *get_currency_code(Currency currency);
const char *get_currency_name(Currency currency);

#endif // ADPRG_MCO_MCO1_9_MODEL_H