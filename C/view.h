//
// Created by Kabligablonk on 10/5/26.
//

#ifndef ADPRG_MCO_VIEW_H
#define ADPRG_MCO_VIEW_H
#include <stdbool.h>
#include <stddef.h>



void view_main_menu_navigation();
void view_account_registration();
void view_deposit_amount();
void view_withdraw_amount();
void view_record_exchange_rate();
void view_foreign_currency_exchange();
int take_int_input(int input, char *prompt, bool show_confirmation);
char take_string_input(char *input, char *prompt, bool show_confirmation);
double take_double_input(double input, char *prompt, bool show_confirmation);
void view_record_exchange_confirmation(int selected_currency, double exchange_rate);

#endif //ADPRG_MCO_VIEW_H
