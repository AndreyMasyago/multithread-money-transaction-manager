package com.github.masyago;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Manager {
    ReentrantLock locker = new ReentrantLock();
    Condition condition = locker.newCondition();

    public void askTransaction(String threadName, Account fromAcc, Account toAcc) {
        locker.lock();
        try {
            System.out.println("Request from " + threadName + ": From  " + fromAcc.getId() + " to " + toAcc.getId());
            while (fromAcc.isLocked() || toAcc.isLocked()) {
                condition.await();
            }

            fromAcc.lock();
            toAcc.lock();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    //TODO: Is locker.lock needed?
    public void unlockResources(String threadName, Account fromAcc, Account toAcc) {
        locker.lock();

        fromAcc.unlock();
        toAcc.unlock();
        condition.signalAll();

        locker.unlock();
    }
}
