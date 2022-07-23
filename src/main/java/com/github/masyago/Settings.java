package com.github.masyago;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {

    private static final int DEFAULT_NUMBER_OF_ACCOUNT = 4;
    private static final int DEFAULT_THREADS_NUMBER = 3;
    private static final int DEFAULT_MAX_TRANSACTIONS_NUMBER = 30;


    private int numOfAccounts = DEFAULT_NUMBER_OF_ACCOUNT;

    private int numOfThreads = DEFAULT_THREADS_NUMBER;

    private int maxTransCount = DEFAULT_MAX_TRANSACTIONS_NUMBER;
}
