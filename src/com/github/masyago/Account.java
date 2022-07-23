package com.github.masyago;

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

    void lock(){
        this.isLocked = true;
    }

    void unlock(){
        this.isLocked = false;
    }

    boolean isLocked(){
        return this.isLocked;
    }
}
