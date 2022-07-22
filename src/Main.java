import java.util.HashMap;
import java.util.Random;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    public static String generateString() {
        String uuid = UUID.randomUUID().toString().substring(0, 3);
        return uuid;
    }

    public static void main(String[] args) {

        int numOfAccounts;
        numOfAccounts = 5;

        int numOfThreads;
        numOfThreads = 2;

        HashMap<Integer, Account> accountsMap = new HashMap<>();
        Manager manager = new Manager();
        Random random = new Random();

        for (int i = 0 ; i < numOfAccounts; i++) {
            String generatedString;
            do {
                generatedString = generateString();
            } while (LazyThread.accountHashMap.containsKey(generatedString));

            Account tmpAccount = new Account(generatedString);
            LazyThread.accountHashMap.put(generatedString, tmpAccount);
        }

        LazyThread.printAccountsState();

        for (int i = 0; i < numOfThreads; i++){
            LazyThread tmpThread = new LazyThread("Thread-"+i, manager);
            tmpThread.start();
        }

/*
        LazyThread t1 = new LazyThread("First", manager);
        t1.start();

        LazyThread t2 = new LazyThread("Second", manager);
        t2.start();
*/

//        Пока кто-то жив: жди
//        Все ммертвы - пиши "Пока - пока"

        return;
        }

    }