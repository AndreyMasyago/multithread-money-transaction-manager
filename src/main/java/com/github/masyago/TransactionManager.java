package com.github.masyago;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.abs;

@Slf4j
@AllArgsConstructor
public class TransactionManager extends Thread {
    private static final int MIN_SLEEP_TIME = 1000;
    private static final int MAX_SLEEP_TIME = 2000;
    private static final int MAX_MONEY_PER_TRANSACTION = 1000;
    private static final AtomicInteger transactionCounter = new AtomicInteger(0);

    private final String threadName;
    private final HashMap<String, Account> accountsHashMap;
    private final AccountManager accountManager;
    private final int maxTransactionsCount;
/*
//  USAGE: for tests only
    public void printAccountsState() {
        System.out.println("========== Current State:");
        accountsHashMap.values().forEach(acc -> System.out.println("key: " + acc.getId() + " money: " + acc.getMoney()));
        System.out.println();
    }
*/

    @Override
    public void run() {
        log.debug("Thread {} started", this.getName());

        Random random = new Random();

        while (true) {
            try {
                int randomSleepTime = random.nextInt(MIN_SLEEP_TIME, MAX_SLEEP_TIME + 1);
                log.debug("Thread {} going to sleep for {} ms", this.threadName, randomSleepTime);
                Thread.sleep(randomSleepTime);
            } catch (InterruptedException e) {
                log.debug("Thread {} interrupted", this.threadName);
            }

            if (transactionCounter.intValue() >= maxTransactionsCount) {
                log.debug("Thread {} exiting. Reached maximum of transactionsCount = {}", this.threadName, maxTransactionsCount);
                return;
            }

            int accountsCount = accountsHashMap.size();

            int money = abs(random.nextInt() % MAX_MONEY_PER_TRANSACTION);
            int from = random.nextInt(0, accountsCount);

            int to = 0;
            do {
                to = random.nextInt(0, accountsCount);
            } while (to == from);

            Account accountTo = accountsHashMap.get(accountsHashMap.keySet().stream().toList().get(to));
            Account accountFrom = accountsHashMap.get(accountsHashMap.keySet().stream().toList().get(from));

            accountManager.askResources(this.threadName, accountFrom, accountTo);

//            USAGE: for tests only
//            printAccountsState();

            if (money > accountFrom.getMoney()) {
                log.info("Denied transaction: {} money from {} to {}. Not enough money: {}",
                        money, accountFrom.getId(), accountTo.getId(), accountFrom.getMoney());
//            USAGE: for tests only
//            printAccountsState();

                accountManager.unlockResources(this.threadName, accountTo, accountFrom);
                continue;
            }

            log.info("Accepted transaction: {} money from {} to {}",
                    money, accountFrom.getId(), accountTo.getId());

            accountFrom.withdrawMoney(money);
            accountTo.putMoney(money);

/*
//          USAGE: for tests only
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
*/

            synchronized (transactionCounter) {
                if (transactionCounter.intValue() < maxTransactionsCount) {

                    transactionCounter.incrementAndGet();
                    accountManager.unlockResources(this.threadName, accountTo, accountFrom);
                } else {
                    log.info("ROLLBACK transaction: {} money from {} to {}", money, accountTo.getId(), accountFrom.getId());

                    accountFrom.putMoney(money);
                    accountTo.withdrawMoney(money);

                    accountManager.unlockResources(this.threadName, accountTo, accountFrom);

                    log.debug("Thread {} exiting. Reached maximum of transactionsCount = {}", this.threadName, maxTransactionsCount);
                    return;
                }
            }
        }
    }
}
