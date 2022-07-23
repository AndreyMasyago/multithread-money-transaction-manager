package com.github.masyago;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Account {
    private static final int defaultStartingMoney = 10000;
    private final String id;
    private Integer money;
    private boolean isLocked;

    public String getId() {
        return id;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public void putMoney(int money) {
        this.money += money;
        log.info("Added {} money to {}. Get {} money", money, this.getId(), this.money);
    }

    public void withdrawMoney(int money) {
        this.money -= money;
        log.info("Withdraw {} money from {}. Get {} money", money, this.getId(), this.money);
    }

    public Account(String id, Integer money) {
        this.id = id;
        this.money = money;
        this.isLocked = false;
    }

    public Account(String id) {
        this.id = id;
        this.money = defaultStartingMoney;
        this.isLocked = false;
    }

    void lock() {
        this.isLocked = true;
    }

    void unlock() {
        this.isLocked = false;
    }

    boolean isLocked() {
        return this.isLocked;
    }
}
