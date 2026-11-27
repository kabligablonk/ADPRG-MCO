#include "MCO2_9_viewmodel.h"

void viewmodel_init(Account *account) {
    init_account(account);
    csv_init(CSV_FILE);
}

int viewmodel_register_account(Account *account, const char *name, double initial_deposit) {
    int result;
    if (initial_deposit < MIN_INITIAL_DEPOSIT) return RESULT_BELOW_MINIMUM;
    result = set_account_name(account, name);
    if (result != RESULT_OK) return result;
    account->account_number = get_next_account_number(CSV_FILE);
    account->balance = initial_deposit;
    write_to_csv(CSV_FILE, account, "deposit", initial_deposit);
    return RESULT_OK;
}

int viewmodel_deposit(Account *account, double amount) {
    int result;
    if (!is_account_registered(account)) return RESULT_NO_ACCOUNT;
    result = deposit_account(account, amount);
    if (result == RESULT_OK) write_to_csv(CSV_FILE, account, "deposit", amount);
    return result;
}

int viewmodel_withdraw(Account *account, double amount) {
    int result;
    if (!is_account_registered(account)) return RESULT_NO_ACCOUNT;
    result = withdraw_account(account, amount);
    if (result == RESULT_OK) write_to_csv(CSV_FILE, account, "withdrawal", amount);
    return result;
}

int viewmodel_record_exchange_rate(Currency currency, double rate) {
    return set_exchange_rate(currency, rate);
}

int viewmodel_convert_currency(Currency from, Currency to, double amount, double *converted) {
    return convert_currency(from, to, amount, converted);
}

double viewmodel_daily_interest(double balance) {
    return daily_interest_calculate(balance);
}

int viewmodel_load_dataset(LoadStats *stats) {
    return report_data_load(CSV_FILE, stats);
}

int viewmodel_generate_reports(ReportSet *set) {
    int result = report_set_generate(set);
    if (result == RESULT_OK) {
        result = report_set_export(set);
    }
    return result;
}
