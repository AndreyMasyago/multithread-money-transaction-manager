package com.github.masyago;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.abs;

@Slf4j
@AllArgsConstructor
public class LazyThread extends Thread {
    //TODO: Refactor naming at constants
    private static final int minSleepTime = 1000;
    private static final int maxSleepTime = 2000;
    private static final int maxMoneyPerTransaction = 1000;
    static final AtomicInteger trans = new AtomicInteger(0);
    private final String threadName;
    private final HashMap<String, Account> accountHashMap;
    private final Manager manager;
    private final int maxTransCount;


//    public LazyThread(HashMap<String, Account> accountHashMap, String name, Manager manager, int maxTransCount) {
//        this.accountHashMap = accountHashMap;
//        this.threadName = name;
//        this.manager = manager;
//        this.maxTransCount = maxTransCount;
//    }

    public void printAccountsState() {
        System.out.println("========== Current State:");
        accountHashMap.values().forEach(acc -> System.out.println("key: " + acc.getId() + " money: " + acc.getMoney()));
        System.out.println();
    }

    @Override
    public void run() {
        log.debug("Thread {} started", this.getName());

        Random random = new Random();

        while (true) {

            try {
                int randomSleepTime = random.nextInt(minSleepTime, maxSleepTime + 1);
                log.debug("Thread {} going to sleep for {} ms", this.threadName, randomSleepTime);
                Thread.sleep(randomSleepTime);
            } catch (InterruptedException e) {
                log.debug("Thread {} interrupted", this.threadName);
            }

            if (trans.intValue() >= maxTransCount) {
                log.debug("Thread {} exiting. Reached maximum of transactionsCount = {}", this.threadName, maxTransCount);
                return;
            }

            int accountsCount = accountHashMap.size();

            int money = abs(random.nextInt() % maxMoneyPerTransaction);
            int from = random.nextInt(0, accountsCount);

            int to = 0;
            do {
                to = random.nextInt(0, accountsCount);
            } while (to == from);

            Account accountTo = accountHashMap.get(accountHashMap.keySet().stream().toList().get(to));
            Account accountFrom = accountHashMap.get(accountHashMap.keySet().stream().toList().get(from));

            manager.askTransaction(this.threadName, accountFrom, accountTo);

//            USAGE: for tests only
//            printAccountsState();

            if (money > accountFrom.getMoney()) {
                log.info("Denied transaction: {} money from {} to {}. Not enough money: {}",
                        money, accountFrom.getId(), accountTo.getId(), accountFrom.getMoney());
//            USAGE: for tests only
//            printAccountsState();

                manager.unlockResources(this.threadName, accountTo, accountFrom);
                continue;
            }

            log.info("Accepted transaction: {} money from {} to {}",
                    money, accountFrom.getId(), accountTo.getId());

            accountFrom.withdrawMoney(money);
            accountTo.putMoney(money);

/*
//            USAGE: for tests only
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
*/

            synchronized (trans) {
                if (trans.intValue() < maxTransCount) {

                    trans.incrementAndGet();
                    manager.unlockResources(this.threadName, accountTo, accountFrom);
                } else {
                    log.info("ROLLBACK transaction: {} money from {} to {}", money, accountTo.getId(), accountFrom.getId());

                    accountFrom.putMoney(money);
                    accountTo.withdrawMoney(money);

                    manager.unlockResources(this.threadName, accountTo, accountFrom);

                    log.debug("Thread {} exiting. Reached maximum of transactionsCount = {}", this.threadName, maxTransCount);
                    return;
                }
            }
        }
    }
}
