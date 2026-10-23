/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#ifndef ADPRG_MCO_MCO1_9_VIEWMODEL_H
#define ADPRG_MCO_MCO1_9_VIEWMODEL_H

#include "MCO1_9_model.h"

void   viewmodel_init(Account *account);
Result viewmodel_register_account(Account *account, const char *name);
Result viewmodel_deposit(Account *account, double amount);
Result viewmodel_withdraw(Account *account, double amount);
Result viewmodel_record_exchange_rate(Currency currency, double rate);
Result viewmodel_convert_currency(Currency from, Currency to, double amount, double *converted);
double viewmodel_daily_interest(double balance);

#endif // ADPRG_MCO_MCO1_9_VIEWMODEL_H