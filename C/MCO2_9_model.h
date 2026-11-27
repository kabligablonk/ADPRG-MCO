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
#define TRANSACTION_TYPE_COUNT 2

#define RESULT_OK 0
#define RESULT_INVALID -1
#define RESULT_NAME_TOO_LONG -2
#define RESULT_NO_ACCOUNT -3
#define RESULT_INSUFFICIENT_FUNDS -4
#define RESULT_BELOW_MINIMUM -5
#define CSV_FILE "bank_transactions.csv"
#define MIN_INITIAL_DEPOSIT 5000.00

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
    unsigned long long account_number;
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
double daily_interest_calculate(double balance);
void csv_init(const char *filename);
unsigned long long get_next_account_number(const char *filename);
void write_to_csv(const char *filename, const Account *account,
                  const char *transaction_type, double amount);
const char *get_currency_code(Currency currency);
const char *get_currency_name(Currency currency);

#endif // ADPRG_MCO_MCO1_9_MODEL_H