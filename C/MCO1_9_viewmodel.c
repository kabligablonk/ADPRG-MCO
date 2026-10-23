/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#include "MCO1_9_viewmodel.h"

void viewmodel_init(Account *account) {
    init_account(account);
}

int viewmodel_register_account(Account *account, const char *name) {
    return set_account_name(account, name);
}

int viewmodel_deposit(Account *account, double amount) {
    if (!is_account_registered(account)) {
        return RESULT_NO_ACCOUNT;
    }
    return deposit_account(account, amount);
}

int viewmodel_withdraw(Account *account, double amount) {
    if (!is_account_registered(account)) {
        return RESULT_NO_ACCOUNT;
    }
    return withdraw_account(account, amount);
}

int viewmodel_record_exchange_rate(Currency currency, double rate) {
    return set_exchange_rate(currency, rate);
}

int viewmodel_convert_currency(Currency from, Currency to, double amount, double *converted) {
    return convert_currency(from, to, amount, converted);
}

double viewmodel_daily_interest(double balance) {
    return compute_daily_interest(balance);
}