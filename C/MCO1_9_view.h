/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#ifndef ADPRG_MCO_MCO1_9_VIEW_H
#define ADPRG_MCO_MCO1_9_VIEW_H

#include <stdbool.h>
#include "MCO1_9_model.h"

int take_int_input(const char *prompt);
int take_choice_input(const char *prompt, int min, int max);
double take_double_input(const char *prompt);
void take_string_input(char *buffer, int buffer_size, const char *prompt);
bool take_yes_no_input(const char *prompt);
void read_line(char *buffer, int buffer_size);
void view_main_menu_navigation(void);
void view_account_registration(Account *account);
void view_deposit_amount(Account *account);
void view_withdraw_amount(Account *account);
void view_foreign_currency_exchange(void);
void view_record_exchange_rate(void);
void view_show_interest_amount(Account *account);

#endif // ADPRG_MCO_MCO1_9_VIEW_H