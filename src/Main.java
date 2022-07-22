import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class Main {
    public static String generateString() {
        return UUID.randomUUID().toString().substring(0, 3);
    }

    public static void main(String[] args) {

        int numOfAccounts;
        numOfAccounts = 6;

        int numOfThreads;
        numOfThreads = 2;

        HashMap<String, Account> accountsMap = new HashMap<>();
        Manager manager = new Manager();

        for (int i = 0 ; i < numOfAccounts; i++) {
            String generatedString;
            do {
                generatedString = generateString();
            } while (accountsMap.containsKey(generatedString));

            Account tmpAccount = new Account(generatedString);
            accountsMap.put(generatedString, tmpAccount);
        }

        System.out.println("========== Current State:");
        accountsMap.values().forEach(acc -> System.out.println("key: "+acc.getId()+" money: "+acc.getMoney()));
        System.out.println();

        for (int i = 0; i < numOfThreads; i++){
            LazyThread tmpThread = new LazyThread(accountsMap, "Thread-"+i, manager);
            tmpThread.start();
        }

//        Пока кто-то жив: жди
//        Все ммертвы - пиши "Пока - пока"

        return;
        }

    }