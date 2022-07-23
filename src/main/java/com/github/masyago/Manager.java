package com.github.masyago;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Manager {
    ReentrantLock locker = new ReentrantLock();
    Condition condition = locker.newCondition();

    public void askTransaction(String threadName, Account fromAcc, Account toAcc) {
        locker.lock();
        try {
            log.debug("Thread {} requested access to {}, {} accounts for transaction ", threadName, fromAcc.getId(), toAcc.getId());
            while (fromAcc.isLocked() || toAcc.isLocked()) {
                log.debug("Thread {} waiting access to {}, {} accounts for transaction ", threadName, fromAcc.getId(), toAcc.getId());
                condition.await();
            }
            log.debug("Thread {} got access to {}, {} accounts for transaction ", threadName, fromAcc.getId(), toAcc.getId());

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
