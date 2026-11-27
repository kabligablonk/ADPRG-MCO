#ifndef ADPRG_MCO_MCO1_9_VIEWMODEL_H
#define ADPRG_MCO_MCO1_9_VIEWMODEL_H

#include "MCO2_9_model.h"
#include "MCO2_reports.h"

void viewmodel_init(Account *account);
int viewmodel_register_account(Account *account, const char *name, double initial_deposit);
int viewmodel_deposit(Account *account, double amount);
int viewmodel_withdraw(Account *account, double amount);
int viewmodel_record_exchange_rate(Currency currency, double rate);
int viewmodel_convert_currency(Currency from, Currency to, double amount, double *converted);
double viewmodel_daily_interest(double balance);
int viewmodel_load_dataset(LoadStats *stats);
int viewmodel_generate_reports(ReportSet *set);

#endif
