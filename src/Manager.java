public class Manager {
    synchronized public boolean askTransaction(Integer id, Account fromAcc, Account toAcc){
        System.out.println("Request from " + id + " thread: From  " + fromAcc.getId() + " to " + toAcc.getId());
        if (fromAcc.isLocked() || toAcc.isLocked()){
            return false;
        }

        fromAcc.lock();
        toAcc.lock();

        return true;
    }
}
