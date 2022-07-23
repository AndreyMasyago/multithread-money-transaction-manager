package com.github.masyago;

import org.apache.commons.cli.*;

public class ArgumentParser {

    public static final String ACCOUNTS_NUMBER_PARAM_NAME = "accountsNumber";
    public static final String THREADS_NUMBER_PARAM_NAME = "threadsNumber";
    public static final String MAX_TRANSACTIONS_NUMBER_PARAM_NAME = "maxTransactionsNumber";

    public Settings parse(String[] args) {
        Options options = new Options();
        Option numberOfAccountsParam = new Option("a", ACCOUNTS_NUMBER_PARAM_NAME, true,
                "Number of accounts");
        Option numberOfThreadsParam = new Option("t", THREADS_NUMBER_PARAM_NAME, true,
                "Number of threads");
        Option maxNumberOfTransactionsParam = new Option("m", MAX_TRANSACTIONS_NUMBER_PARAM_NAME, true,
                "Maximum number of transactions");
        options.addOption(numberOfAccountsParam);
        options.addOption(numberOfThreadsParam);
        options.addOption(maxNumberOfTransactionsParam);

        CommandLineParser parser = new DefaultParser();
        Settings settings = new Settings();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(ACCOUNTS_NUMBER_PARAM_NAME)) {
                settings.setNumOfAccounts(Integer.parseInt(line.getOptionValue(ACCOUNTS_NUMBER_PARAM_NAME)));
            }

            if (line.hasOption(THREADS_NUMBER_PARAM_NAME)) {
                settings.setNumOfThreads(Integer.parseInt(line.getOptionValue(THREADS_NUMBER_PARAM_NAME)));
            }

            if (line.hasOption(MAX_TRANSACTIONS_NUMBER_PARAM_NAME)) {
                settings.setMaxTransCount(Integer.parseInt(line.getOptionValue(MAX_TRANSACTIONS_NUMBER_PARAM_NAME)));
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return settings;
    }
}
