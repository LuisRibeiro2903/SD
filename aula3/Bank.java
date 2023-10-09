import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private static class Account {
        private int balance;

        //Account lock
        final ReentrantLock l = new ReentrantLock();
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


    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av; 

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        Account a = av[id];
        a.l.lock();
        try{
            return a.balance();
        } finally {
            a.l.unlock();
        }
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        Account a = av[id];
        a.l.lock();
        try{
            return a.deposit(value);
        } finally {
            a.l.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        Account a = av[id];
        a.l.lock();
        try {
            return a.withdraw(value);
        } finally {
            a.l.unlock();
        }
    }

    public boolean transfer (int from, int to, int value) {
        if((from < 0 || from >= slots) || (to < 0 || to >= slots))
            return false;
        Account f = av[from];
        Account t = av[to];
        if(from < to) {
            f.l.lock();
            t.l.lock();
        } else {
            t.l.lock();
            f.l.lock();
        }
        try {
            try {
                if (!withdraw(from, value))
                    return false;
            } finally {
                f.l.unlock();
            }
            return deposit(to, value);
        } finally {
            t.l.unlock();
        }
        
    }

    public int totalBalance () {
        int total = 0;
        for(int i = 0; i < slots; i++)
            av[i].l.lock();
        for(int i = 0; i < slots; i++){
            total += balance(i);
            av[i].l.unlock();
        }

        /* O Professor prefere assim:

        for(int i = 0; i < slots; i++) {
            av[i].l.lock();
            total +=balance(i);
        }
        for(int i = 0; i < slots; i++)
            av[i].l.unlock();
        */

        return total;
    }
}
