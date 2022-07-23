package com.github.masyago;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class AccountManager {
    private final ReentrantLock locker = new ReentrantLock();
    private final Condition condition = locker.newCondition();

    public void askResources(String threadName, Account fromAcc, Account toAcc) {
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

    public void unlockResources(String threadName, Account fromAcc, Account toAcc) {
        locker.lock();

        fromAcc.unlock();
        toAcc.unlock();
        log.debug("Thread {} return access to {}, {} accounts", threadName, fromAcc.getId(), toAcc.getId());
        condition.signalAll();

        locker.unlock();
    }
}
