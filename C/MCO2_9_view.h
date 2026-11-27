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
#include "MCO2_9_model.h"

int prompt_for_integer(const char *prompt);
int prompt_for_bounded_choice(const char *prompt, int min, int max);
double prompt_for_double(const char *prompt);
void prompt_for_string(char *buffer, const char *prompt);
bool prompt_for_yes_no(const char *prompt);
void run_main_menu(void);
void handle_account_registration(Account *account);
void handle_deposit(Account *account);
void handle_withdrawal(Account *account);
void handle_currency_exchange(void);
void handle_exchange_rate_update(void);
void display_interest_projection(Account *account);
void run_report_menu(void);

#endif // ADPRG_MCO_MCO1_9_VIEW_H