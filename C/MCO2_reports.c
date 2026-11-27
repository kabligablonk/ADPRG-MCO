/*
*******************
* Language: C
* Paradigm(s): Procedural programming
* MCO2: data ingestion + report generation (REQ-0003 to REQ-0010)
*******************
*/

#include "MCO2_reports.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LINE_SIZE 512
#define MAX_LOGGED_ERRORS 5
#define MAX_MONTH_DAYS 31

typedef struct {
    char name[ACCOUNT_NAME_SIZE];
    unsigned long long account_number;
    int year, month, dom;
    long day;
    long seconds;
    double amount;
    int is_deposit;
    double balance;
    size_t seq;
} Txn;

static Txn *g_txns = NULL;
static size_t g_count = 0;


static int is_leap_year(int y) {
    return (y % 4 == 0 && y % 100 != 0) || y % 400 == 0;
}

static int days_in_calendar_month(int y, int m) {
    static const int table[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    if (m == 2 && is_leap_year(y)) {
        return 29;
    }
    return table[m - 1];
}

static long civil_date_to_epoch_days(int y, int m, int d) {
    long era;
    unsigned yoe, doy, doe;

    y -= (m <= 2);
    era = (y >= 0 ? y : y - 399) / 400;
    yoe = (unsigned)(y - era * 400);
    doy = (unsigned)((153 * (m + (m > 2 ? -3 : 9)) + 2) / 5 + d - 1);
    doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
    return era * 146097 + (long)doe - 719468;
}

static const char *parse_transaction_row(const char *line, Txn *t, char *currency) {
    char type[16];
    int h, m, s, used = 0;

    if (sscanf(line, "%31[^,],%llu,%d/%d/%d,%d:%d:%d,%7[^,],%lf,%15[^,],%lf%n",
               t->name, &t->account_number, &t->dom, &t->month, &t->year,
               &h, &m, &s, currency, &t->amount, type, &t->balance, &used) != 12) {
        return "malformed row or missing value";
    }
    if (line[used + strspn(line + used, " \r\n")] != '\0') {
        return "too many fields";
    }
    if (t->account_number == 0) {
        return "invalid account number";
    }
    if (t->year < 1900 || t->year > 2100 || t->month < 1 || t->month > 12 ||
        t->dom < 1 || t->dom > days_in_calendar_month(t->year, t->month)) {
        return "invalid date";
    }
    if (h < 0 || h > 23 || m < 0 || m > 59 || s < 0 || s > 59) {
        return "invalid time";
    }
    if (!isfinite(t->amount) || t->amount <= 0 || !isfinite(t->balance)) {
        return "invalid amount";
    }
    if (strcmp(type, "deposit") == 0) {
        t->is_deposit = 1;
    } else if (strcmp(type, "withdrawal") != 0) {
        return "invalid transaction type";
    }
    t->day = civil_date_to_epoch_days(t->year, t->month, t->dom);
    t->seconds = h * 3600L + m * 60L + s;
    return NULL;
}

static int compare_transactions_by_timestamp(const void *a, const void *b) {
    const Txn *x = (const Txn *)a;
    const Txn *y = (const Txn *)b;

    if (x->day != y->day) return x->day < y->day ? -1 : 1;
    if (x->seconds != y->seconds) return x->seconds < y->seconds ? -1 : 1;
    if (x->seq != y->seq) return x->seq < y->seq ? -1 : 1;
    return 0;
}

void report_data_clear(void) {
    free(g_txns);
    g_txns = NULL;
    g_count = 0;
}

int report_data_load(const char *filename, LoadStats *stats) {
    FILE *file = fopen(filename, "r");
    char line[LINE_SIZE];
    char currency[8];
    size_t lines = 0, line_no = 1;

    memset(stats, 0, sizeof(*stats));
    report_data_clear();
    if (!file) {
        return RESULT_INVALID;
    }

    while (fgets(line, sizeof(line), file)) {
        lines++;
    }
    rewind(file);
    g_txns = (Txn *)malloc((lines ? lines : 1) * sizeof(Txn));
    if (!g_txns) {
        fclose(file);
        return RESULT_INVALID;
    }
    if (!fgets(line, sizeof(line), file)) {
        fclose(file);
        return RESULT_OK;
    }

    while (fgets(line, sizeof(line), file)) {
        Txn t;
        const char *reason;

        line_no++;
        if (line[strspn(line, " \r\n")] == '\0') {
            continue;
        }
        stats->rows_read++;
        memset(&t, 0, sizeof(t));
        reason = parse_transaction_row(line, &t, currency);

        if (reason) {
            stats->rows_invalid++;
            if (stats->rows_invalid <= MAX_LOGGED_ERRORS) {
                printf("  [skipped] line %zu: %s\n", line_no, reason);
            }
        } else if (strcmp(currency, "PHP") == 0) {   /* REQ-0003: PHP only */
            t.seq = stats->rows_read;
            g_txns[g_count++] = t;
            stats->rows_php++;
        }
    }
    fclose(file);

    if (stats->rows_invalid > MAX_LOGGED_ERRORS) {
        printf("  ... and %zu more skipped rows\n", stats->rows_invalid - MAX_LOGGED_ERRORS);
    }
    qsort(g_txns, g_count, sizeof(Txn), compare_transactions_by_timestamp);
    return RESULT_OK;
}

typedef struct {
    char name[ACCOUNT_NAME_SIZE];
    unsigned long long number;
    long open_day;
    double balance;
    double carry;
    double eod[MAX_MONTH_DAYS];
    char touched[MAX_MONTH_DAYS];
} AcctInfo;


static AcctInfo *find_or_create_account_info(AcctInfo *list, size_t *count, const Txn *t) {
    size_t i;
    AcctInfo *a;

    for (i = 0; i < *count; i++) {
        if (list[i].number == t->account_number) {
            return &list[i];
        }
    }
    a = &list[(*count)++];
    snprintf(a->name, sizeof(a->name), "%s", t->name);
    a->number = t->account_number;
    a->open_day = t->day;
    return a;
}

/* ------------------------------------------------------------------ */
/* Sorting                                                             */
/* ------------------------------------------------------------------ */
static int compare_privilege_tier_rows(const void *a, const void *b) {
    const Report1Row *x = (const Report1Row *)a;
    const Report1Row *y = (const Report1Row *)b;

    if (x->average_daily_balance != y->average_daily_balance) {
        return x->average_daily_balance < y->average_daily_balance ? 1 : -1;
    }
    return strcmp(x->name, y->name);
}

static int compare_interest_projection_rows(const void *a, const void *b) {
    const Report2Row *x = (const Report2Row *)a;
    const Report2Row *y = (const Report2Row *)b;

    if (x->expected_interest != y->expected_interest) {
        return x->expected_interest < y->expected_interest ? 1 : -1;
    }
    return strcmp(x->name, y->name);
}

static int compare_suspicious_behavior_rows(const void *a, const void *b) {
    const Report3Row *x = (const Report3Row *)a;
    const Report3Row *y = (const Report3Row *)b;
    long kx = x->year * 10000L + x->month * 100L + x->day;
    long ky = y->year * 10000L + y->month * 100L + y->day;

    if (kx != ky) return kx < ky ? -1 : 1;                       /* date ascending  */
    if (x->suspicious != y->suspicious) return y->suspicious - x->suspicious; /* Suspicious first */
    return strcmp(x->name, y->name);
}

static int compare_loyalty_score_rows(const void *a, const void *b) {
    const Report4Row *x = (const Report4Row *)a;
    const Report4Row *y = (const Report4Row *)b;

    if (x->loyalty_score != y->loyalty_score) {
        return x->loyalty_score < y->loyalty_score ? 1 : -1;
    }
    return strcmp(x->name, y->name);
}

static int compare_transactions_by_account_and_day(const void *a, const void *b) {
    const Txn *x = (const Txn *)a;
    const Txn *y = (const Txn *)b;

    if (x->account_number != y->account_number) {
        return x->account_number < y->account_number ? -1 : 1;
    }
    if (x->day != y->day) return x->day < y->day ? -1 : 1;
    return 0;
}

static const char *privilege_tier_for_balance(double adb) {
    if (adb < REPORT_SILVER_LIMIT) return "Silver";
    if (adb < REPORT_GOLD_LIMIT) return "Gold";
    return "Platinum";
}

static int build_suspicious_behavior_report(ReportSet *out) {
    Txn *sorted = (Txn *)malloc(g_count * sizeof(Txn));
    size_t i = 0;

    out->r3 = (Report3Row *)malloc(g_count * sizeof(Report3Row));
    if (!sorted || !out->r3) {
        free(sorted);
        return -1;
    }
    memcpy(sorted, g_txns, g_count * sizeof(Txn));
    qsort(sorted, g_count, sizeof(Txn), compare_transactions_by_account_and_day);

    while (i < g_count) {
        const Txn *first = &sorted[i];
        Report3Row row;

        memset(&row, 0, sizeof(row));
        snprintf(row.name, sizeof(row.name), "%s", first->name);
        row.year = first->year;
        row.month = first->month;
        row.day = first->dom;

        for (; i < g_count && sorted[i].account_number == first->account_number &&
               sorted[i].day == first->day; i++) {
            if (sorted[i].is_deposit) {
                row.deposit_count++;
                row.total_deposits += sorted[i].amount;
            } else {
                row.total_withdrawals += sorted[i].amount;
            }
        }

        if (row.total_deposits > 0) {
            row.has_turnover = 1;
            row.turnover_velocity = row.total_withdrawals / row.total_deposits * 100.0;
        }
        row.suspicious = row.total_withdrawals >= REPORT_SUSPICIOUS_WITHDRAWAL ||
                         (row.has_turnover && row.turnover_velocity >= REPORT_SUSPICIOUS_TURNOVER);
        out->r3[out->n3++] = row;
    }
    free(sorted);
    qsort(out->r3, out->n3, sizeof(Report3Row), compare_suspicious_behavior_rows);
    return 0;
}

int report_set_generate(ReportSet *out) {
    AcctInfo *accts;
    size_t acct_count = 0, i;
    int ref_year, ref_month, ref_days, d;
    long month_start, as_of_day;
    int status = RESULT_INVALID;

    memset(out, 0, sizeof(*out));
    if (g_count == 0) {
        return RESULT_INVALID;
    }

    ref_year = g_txns[g_count - 1].year;
    ref_month = g_txns[g_count - 1].month;
    ref_days = days_in_calendar_month(ref_year, ref_month);
    month_start = civil_date_to_epoch_days(ref_year, ref_month, 1);
    as_of_day = g_txns[g_count - 1].day;

    accts = (AcctInfo *)calloc(g_count, sizeof(AcctInfo));
    if (!accts) {
        return RESULT_INVALID;
    }


    for (i = 0; i < g_count; i++) {
        const Txn *t = &g_txns[i];
        AcctInfo *a = find_or_create_account_info(accts, &acct_count, t);

        a->balance = t->balance;
        if (t->day >= month_start) {
            a->touched[t->dom - 1] = 1;
            a->eod[t->dom - 1] = t->balance;
        } else {
            a->carry = t->balance;
        }
        if (t->is_deposit) {
            out->summary.total_deposits += t->amount;
        } else {
            out->summary.total_withdrawals += t->amount;
        }
    }
    out->summary.total_accounts = acct_count;

    out->r1 = (Report1Row *)calloc(acct_count, sizeof(Report1Row));
    out->r2 = (Report2Row *)calloc(acct_count, sizeof(Report2Row));
    out->r4 = (Report4Row *)calloc(acct_count, sizeof(Report4Row));
    if (!out->r1 || !out->r2 || !out->r4) {
        goto done;
    }

    for (i = 0; i < acct_count; i++) {
        AcctInfo *a = &accts[i];
        double prev = a->carry;
        double sum = 0.0, adb, projected;
        int day;

        out->summary.total_balance += a->balance;

        for (d = 0; d < ref_days; d++) {
            if (a->touched[d]) {
                prev = a->eod[d];
            }
            sum += prev;
        }
        adb = sum / ref_days;
        if (adb >= REPORT_MIN_ADB) {
            Report1Row *r = &out->r1[out->n1++];

            snprintf(r->name, sizeof(r->name), "%s", a->name);
            r->account_age = as_of_day - a->open_day;
            r->current_balance = a->balance;
            r->average_daily_balance = adb;
            r->tier = privilege_tier_for_balance(adb);
        }


        {
            Report4Row *r = &out->r4[out->n4++];
            long age = as_of_day - a->open_day;

            if (age < LOYALTY_MIN_AGE_DAYS) {
                age = LOYALTY_MIN_AGE_DAYS;
            }
            snprintf(r->name, sizeof(r->name), "%s", a->name);
            r->account_age = age;
            r->current_balance = a->balance;
            r->raw_index = a->balance / (double)age * LOYALTY_FACTOR;
        }

        if (a->balance >= REPORT_MIN_INTEREST_BAL) {
            Report2Row *r = &out->r2[out->n2++];

            projected = a->balance;
            for (day = 0; day < REPORT_PROJECTION_DAYS; day++) {
                projected += daily_interest_calculate(projected);
            }
            snprintf(r->name, sizeof(r->name), "%s", a->name);
            r->current_balance = a->balance;
            r->expected_interest = projected - a->balance;
            out->summary.total_interest_expense += r->expected_interest;
        }
    }
    {
        double max_raw = 0.0;

        for (i = 0; i < out->n4; i++) {
            if (out->r4[i].raw_index > max_raw) {
                max_raw = out->r4[i].raw_index;
            }
        }
        for (i = 0; i < out->n4; i++) {
            out->r4[i].loyalty_score = max_raw > 0 ? out->r4[i].raw_index / max_raw * 100.0 : 0.0;
        }
    }
    qsort(out->r4, out->n4, sizeof(Report4Row), compare_loyalty_score_rows);
    qsort(out->r1, out->n1, sizeof(Report1Row), compare_privilege_tier_rows);
    qsort(out->r2, out->n2, sizeof(Report2Row), compare_interest_projection_rows);

    if (build_suspicious_behavior_report(out) != 0) {
        goto done;
    }
    status = RESULT_OK;

done:
    free(accts);
    if (status != RESULT_OK) {
        report_set_free(out);
    }
    return status;
}

void report_set_free(ReportSet *set) {
    free(set->r1);
    free(set->r2);
    free(set->r3);
    free(set->r4);
    memset(set, 0, sizeof(*set));
}

static void format_currency_amount(double value, char *out, size_t size) {
    char raw[64];
    const char *p = raw;
    const char *dot;
    size_t int_len, i, pos = 0;

    snprintf(raw, sizeof(raw), "%.2f", value);
    if (*p == '-') {
        out[pos++] = '-';
        p++;
    }
    dot = strchr(p, '.');
    int_len = dot ? (size_t)(dot - p) : strlen(p);
    for (i = 0; i < int_len && pos + 2 < size; i++) {
        out[pos++] = p[i];
        if ((int_len - 1 - i) > 0 && (int_len - 1 - i) % 3 == 0) {
            out[pos++] = ',';
        }
    }
    out[pos] = '\0';
    if (dot) {
        strncat(out, dot, size - strlen(out) - 1);
    }
}

static void write_quoted_currency_amount(FILE *file, double value) {
    char buf[64];

    format_currency_amount(value, buf, sizeof(buf));
    fprintf(file, "\"%s\"", buf);
}

static FILE *open_report_file(const char *path, const char *header) {
    FILE *file = fopen(path, "w");

    if (file) {
        fprintf(file, "%s\n", header);
    }
    return file;
}

int report_set_export(const ReportSet *set) {
    FILE *file;
    size_t i;

    file = open_report_file(REPORT1_FILE, "AccountName,AccountAge,CurrentBalance,AverageDailyBalance,PrivilegeTier");
    if (!file) return RESULT_INVALID;
    for (i = 0; i < set->n1; i++) {
        const Report1Row *r = &set->r1[i];

        fprintf(file, "%s,%ld,", r->name, r->account_age);
        write_quoted_currency_amount(file, r->current_balance);
        fputc(',', file);
        write_quoted_currency_amount(file, r->average_daily_balance);
        fprintf(file, ",%s\n", r->tier);
    }
    fclose(file);

    file = open_report_file(REPORT2_FILE, "AccountName,CurrentBalance,ExpectedInterest");
    if (!file) return RESULT_INVALID;
    for (i = 0; i < set->n2; i++) {
        const Report2Row *r = &set->r2[i];

        fprintf(file, "%s,", r->name);
        write_quoted_currency_amount(file, r->current_balance);
        fputc(',', file);
        write_quoted_currency_amount(file, r->expected_interest);
        fputc('\n', file);
    }
    fclose(file);

    file = open_report_file(REPORT3_FILE, "AccountName,Date,TotalWithdrawalAmounts,NumberOfDeposits,"
                                     "TotalDepositAmounts,TurnoverVelocity(%),RiskFlagLevel");
    if (!file) return RESULT_INVALID;
    for (i = 0; i < set->n3; i++) {
        const Report3Row *r = &set->r3[i];

        fprintf(file, "%s,%02d/%02d/%04d,", r->name, r->day, r->month, r->year);
        write_quoted_currency_amount(file, r->total_withdrawals);
        fprintf(file, ",%d,", r->deposit_count);
        write_quoted_currency_amount(file, r->total_deposits);
        fputc(',', file);
        if (r->has_turnover) {
            write_quoted_currency_amount(file, r->turnover_velocity);
        } else {
            fprintf(file, "N/A");
        }
        fprintf(file, ",%s\n", r->suspicious ? "Suspicious" : "Normal");
    }
    fclose(file);

    file = open_report_file(REPORT4_FILE, "AccountName,AccountAge,CurrentBalance,RawIndex,LoyaltyScore");
    if (!file) return RESULT_INVALID;
    for (i = 0; i < set->n4; i++) {
        const Report4Row *r = &set->r4[i];

        fprintf(file, "%s,%ld,", r->name, r->account_age);
        write_quoted_currency_amount(file, r->current_balance);
        fputc(',', file);
        write_quoted_currency_amount(file, r->raw_index);
        fputc(',', file);
        write_quoted_currency_amount(file, r->loyalty_score);
        fputc('\n', file);
    }
    fclose(file);

    file = fopen(SUMMARY_FILE, "w");
    if (!file) return RESULT_INVALID;
    fprintf(file, "{\"total_accounts\": %zu, \"total_deposits\": %.2f, "
                  "\"total_withdrawals\": %.2f, \"total_balance\": %.2f, "
                  "\"total_interest_expense\": %.2f}\n",
            set->summary.total_accounts, set->summary.total_deposits,
            set->summary.total_withdrawals, set->summary.total_balance,
            set->summary.total_interest_expense);
    fclose(file);
    return RESULT_OK;
}