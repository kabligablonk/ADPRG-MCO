/*
*******************
* Language: C
* Paradigm(s): Procedural programming
* MCO2: data ingestion + report generation (REQ-0003 to REQ-0010)
*******************
*/

#ifndef MCO2_REPORTS_H
#define MCO2_REPORTS_H

#include <stddef.h>
#include "MCO2_9_model.h"

#define REPORT_MIN_ADB          100000.00   /* Report 1 filter */
#define REPORT_SILVER_LIMIT     500000.00
#define REPORT_GOLD_LIMIT       1000000.00
#define REPORT_MIN_INTEREST_BAL 10000.00    /* Report 2 filter */
#define REPORT_PROJECTION_DAYS  30
#define REPORT_SUSPICIOUS_WITHDRAWAL 500000.00
#define REPORT_SUSPICIOUS_TURNOVER   90.0

#define REPORT1_FILE "report1_banking_privilege_tiers.csv"
#define REPORT2_FILE "report2_interest_expense_projection.csv"
#define REPORT3_FILE "report3_suspicious_client_behavior.csv"
#define REPORT4_FILE "report4_loyalty_score.csv"
#define SUMMARY_FILE "summary.json"

/* Report 4 (custom): raw index = (CurrentBalance / AccountAgeInDays) * LOYALTY_FACTOR.
   The raw index is unbounded, so the score is scaled so the top account = 100. */
#define LOYALTY_FACTOR 0.8
#define LOYALTY_MIN_AGE_DAYS 1   /* avoids division by zero for brand-new accounts */

/* ---- ingestion statistics (REQ-0003/0004) ---- */
typedef struct {
    size_t rows_read;      /* data rows in the file (header excluded)   */
    size_t rows_php;       /* valid rows in PHP (the ones kept)         */
    size_t rows_invalid;   /* rows rejected: malformed, missing value, bad date/amount */
} LoadStats;

/* ---- report rows ---- */
typedef struct {
    char name[ACCOUNT_NAME_SIZE];
    long account_age;
    double current_balance;
    double average_daily_balance;
    const char *tier;
} Report1Row;

typedef struct {
    char name[ACCOUNT_NAME_SIZE];
    double current_balance;
    double expected_interest;
} Report2Row;

typedef struct {
    char name[ACCOUNT_NAME_SIZE];
    int year, month, day;
    double total_withdrawals;
    int deposit_count;
    double total_deposits;
    int has_turnover;          /* 0 when there were no deposits that day */
    double turnover_velocity;  /* percent */
    int suspicious;
} Report3Row;

typedef struct {
    char name[ACCOUNT_NAME_SIZE];
    long account_age;
    double current_balance;
    double raw_index;        /* (balance / age) * 0.8, unbounded */
    double loyalty_score;    /* 0-100, raw_index scaled against the highest account */
} Report4Row;

typedef struct {
    size_t total_accounts;
    double total_deposits;
    double total_withdrawals;
    double total_balance;
    double total_interest_expense;
} ReportSummary;

typedef struct {
    Report1Row *r1; size_t n1;
    Report2Row *r2; size_t n2;
    Report3Row *r3; size_t n3;
    Report4Row *r4; size_t n4;
    ReportSummary summary;
} ReportSet;

/* Reads + validates + cleans the CSV into module-owned memory. Returns RESULT_OK or RESULT_INVALID. */
int report_data_load(const char *filename, LoadStats *stats);
/* Builds all reports from the loaded data. */
int report_set_generate(ReportSet *out);
/* Writes the three report CSVs and summary.json. */
int report_set_export(const ReportSet *set);
void report_set_free(ReportSet *set);
void report_data_clear(void);

#endif