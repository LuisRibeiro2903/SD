import java.util.concurrent.locks.ReentrantLock;

public class Bankmal {

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank universal lock
    private ReentrantLock l;

    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av; 

    public Bankmal(int n) {
        slots=n;
        av=new Account[slots];
        l = new ReentrantLock();
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        this.l.lock();
        try{
            return av[id].balance();
        } finally {
            this.l.unlock();
        }
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        this.l.lock();
        try{
            return av[id].deposit(value);
        }
        finally{
            this.l.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        this.l.lock();
        try{
            return av[id].withdraw(value);
        }
        finally{
            this.l.unlock();
        }
    }

    public boolean transfer (int from, int to, int value) {
        if((from < 0 || from >= slots) || (to < 0 || to >= slots))
            return false;
        this.l.lock();
        try{
            return (withdraw(from, value) && deposit(to, value));
        } finally {
            this.l.unlock();
        }
        
    }

    public int totalBalance () {
        int total = 0;
        this.l.lock();
        try{
            for(int i = 0; i < slots; i++) total += balance(i);
            return total;
        } finally {
            this.l.unlock();
        }
    }
}
