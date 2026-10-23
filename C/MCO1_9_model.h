/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#ifndef ADPRG_MCO_MCO1_9_MODEL_H
#define ADPRG_MCO_MCO1_9_MODEL_H

#define ACCOUNT_NAME_SIZE 32
#define CURRENCY_COUNT 6
#define ANNUAL_INTEREST_RATE 0.05

/* Return codes used by the model and viewmodel */
#define RESULT_OK 0
#define RESULT_INVALID -1
#define RESULT_NAME_TOO_LONG -2
#define RESULT_NO_ACCOUNT -3
#define RESULT_INSUFFICIENT_FUNDS -4

typedef enum {
    PHP,
    USD,
    JPY,
    GBP,
    EUR,
    CNY
} Currency;

typedef struct {
    char account_name[ACCOUNT_NAME_SIZE];
    double balance;
    Currency currency;
} Account;

void init_account(Account *account);
int set_account_name(Account *account, const char *name);
int is_account_registered(const Account *account);
int deposit_account(Account *account, double amount);
int withdraw_account(Account *account, double amount);
int set_exchange_rate(Currency currency, double rate);
int get_exchange_rate(Currency currency, double *rate);
int convert_currency(Currency from, Currency to, double amount, double *converted);
double compute_daily_interest(double balance);
const char *get_currency_code(Currency currency);
const char *get_currency_name(Currency currency);

#endif // ADPRG_MCO_MCO1_9_MODEL_H