package com.github.masyago;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Main {
    public static String generateString() {
//        USAGE: for easier testing
        return UUID.randomUUID().toString().substring(0, 3);
//        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {

        int numOfAccounts;
        numOfAccounts = 6;

        int numOfThreads;
        numOfThreads = 5;

        int maxTransCount;
        maxTransCount = 6;

        HashMap<String, Account> accountsMap = new HashMap<>();
        Manager manager = new Manager();

        for (int i = 0; i < numOfAccounts; i++) {
            String generatedString;
            do {
                generatedString = generateString();
            } while (accountsMap.containsKey(generatedString));

            Account tmpAccount = new Account(generatedString);
            log.info("Created new account {} with {} money", tmpAccount.getId(), tmpAccount.getMoney());
            accountsMap.put(generatedString, tmpAccount);
        }
/*

        System.out.println("========== Current State:");
        accountsMap.values().forEach(acc -> System.out.println("key: " + acc.getId() + " money: " + acc.getMoney()));
        System.out.println();

*/
        List<LazyThread> listOfThreads = new ArrayList<>();

        for (int i = 0; i < numOfThreads; i++) {
            LazyThread tmpThread = new LazyThread("Thread-" + i, accountsMap, manager, maxTransCount);
            log.debug("Thread {} created", tmpThread.getName());
            listOfThreads.add(tmpThread);
            tmpThread.start();
        }

        listOfThreads.forEach(lazyThread -> {
            try {
                lazyThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        Пока кто-то жив: жди
//        Все ммертвы - пиши "Пока - пока"
/*

        System.out.println("\nFinal State:\n");
        accountsMap.values().forEach(acc -> System.out.println("key: " + acc.getId() + " money: " + acc.getMoney()));
        System.out.println();

*/
    }

}