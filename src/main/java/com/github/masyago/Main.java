package com.github.masyago;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class Main {
    public static String generateString() {
//        USAGE: for easier testing
//        return UUID.randomUUID().toString().substring(0, 3);
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        int numOfAccounts;
        numOfAccounts = 6;

        int numOfThreads;
        numOfThreads = 5;

        int maxTransCount;
        maxTransCount = 6;

        HashMap<String, Account> accountsMap = new HashMap<>();
        AccountManager accountManager = new AccountManager();

        for (int i = 0; i < numOfAccounts; i++) {
            String generatedString;
            do {
                generatedString = generateString();
            } while (accountsMap.containsKey(generatedString));

            Account tmpAccount = new Account(generatedString);
            log.info("Created new account {} with {} money", tmpAccount.getId(), tmpAccount.getMoney());
            accountsMap.put(generatedString, tmpAccount);
        }


        for (int i = 0; i < numOfThreads; i++) {
            TransactionManager tmpThread = new TransactionManager("Thread-" + i, accountsMap, accountManager, maxTransCount);
            log.debug("Thread {} created", tmpThread.getName());
            tmpThread.start();
        }
    }
}