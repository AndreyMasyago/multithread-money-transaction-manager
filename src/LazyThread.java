import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.abs;

public class LazyThread extends Thread {
    static final AtomicInteger trans = new AtomicInteger(0);
    private final HashMap<String, Account> accountHashMap;
    private final Manager manager;

    String threadName;

    public LazyThread(HashMap<String, Account> accountHashMap, String name, Manager manager) {
        this.accountHashMap = accountHashMap;
        this.threadName = name;
        this.manager = manager;
    }

    // TODO: is it correct? Static?!
    public void printAccountsState() {
        System.out.println("========== Current State:");
        accountHashMap.values().forEach(acc -> System.out.println("key: " + acc.getId() + " money: " + acc.getMoney()));
        System.out.println();
    }

    @Override
    public void run() {
        System.out.println("Running " + this.getName() + " Thread");
        Random random = new Random();
        while (true) {

            try {
                int randomSleepTime = random.nextInt(1000, 2001);
                System.out.println(this.getName() + " going for sleep " + randomSleepTime + "ms");
                Thread.sleep(randomSleepTime);
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getName() + " interrupted.");
            }

            if (trans.intValue() >= 5) {
                System.out.println("Trans >= 5. " + this.getName() + " is exiting.");
//                this.interrupt();
                //todo обработай exit
                return;
            }

            int accountsCount = accountHashMap.size();

            int money = abs(random.nextInt() % 10000);
            int from = random.nextInt(0, accountsCount);

            int to = 0;
            do {
                to = random.nextInt(0, accountsCount);
            } while (to == from);

            Account accountTo = accountHashMap.get(accountHashMap.keySet().stream().toList().get(to));
            Account accountFrom = accountHashMap.get(accountHashMap.keySet().stream().toList().get(from));


            //TODO: do smth with ID
            while (!manager.askTransaction((int) this.getId(), accountTo, accountFrom)) {
                //TODO: DELETE THIS. (Useless stuff)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            printAccountsState();

            //Doing some work and unlock
            System.out.println(money + " money from " + from + " to " + to);

            if (money > accountFrom.getMoney()) {
                System.out.println("Not enough money for transaction");
                System.out.println("Asking: " + money + ". At " + from + "th account " + accountFrom.getMoney());

                printAccountsState();

                accountTo.unlock();
                accountFrom.unlock();
                continue;
            }

            accountFrom.setMoney(accountFrom.getMoney() - money);
            accountTo.setMoney(accountTo.getMoney() + money);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            synchronized (trans) {
                if (trans.intValue() < 5) {

                    trans.incrementAndGet();
                    accountFrom.unlock();
                    accountTo.unlock();
                } else {
                    System.out.println("ROLLBACK");
                    System.out.println(money + " money from " + to + " from " + from);

                    accountFrom.setMoney(accountFrom.getMoney() + money);
                    accountTo.setMoney(accountTo.getMoney() - money);

//                    this.interrupt();
                    //todo обработай exit
                    return;
                }
            }
        }
    }
}
