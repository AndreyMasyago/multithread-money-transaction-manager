package com.github.masyago;

public class Manager {
    synchronized public boolean askTransaction(String threadName, Account fromAcc, Account toAcc){
        System.out.println("Request from " + threadName + ": From  " + fromAcc.getId() + " to " + toAcc.getId());
        while (fromAcc.isLocked() || toAcc.isLocked()){
            try{
                wait();
                System.out.println("AA-a-a-a-a");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        fromAcc.lock();
        toAcc.lock();


        return true;
    }

    //TODO: Lock and conditions usage?
    synchronized public void unlockResources(String threadName, Account fromAcc, Account toAcc){
        fromAcc.unlock();
        toAcc.unlock();
        notifyAll();
    }
}
