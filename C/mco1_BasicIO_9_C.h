/*
*******************
* Last names: Covar, Lising, Miranda, Tiotuyco
* Language: C
* Paradigm(s): Procedural programming
*******************
*/

#ifndef ADPRG_MCO_VIEW_H
#define ADPRG_MCO_VIEW_H

#include <stdbool.h>

void view_main_menu_navigation(void);
void view_account_registration(void);
void view_deposit_amount(void);
void view_withdraw_amount(void);
void view_record_exchange_rate(void);
void view_foreign_currency_exchange(void);
int take_int_input(const char *prompt, bool show_confirmation);
void take_string_input(char *buffer, int buffer_size, const char *prompt, bool show_confirmation);
double take_double_input(const char *prompt, bool show_confirmation);

#endif // ADPRG_MCO_VIEW_H