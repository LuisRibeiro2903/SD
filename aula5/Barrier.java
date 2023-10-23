package aula5;

import java.util.concurrent.locks.*;

public class Barrier {

    private ReentrantLock l = new ReentrantLock();
    private Condition cond = l.newCondition();
    private int c = 0;
    private int e = 0;
    private final int N;

    Barrier (int N) { 
        this.N = N;
    }


    void await() throws InterruptedException {
     
        l.lock();
        try {
            int e = this.e;
            this.c++;
            if (this.c == N)
            {
                this.cond.signalAll();
                this.c = 0;
                e++;
            } else while(this.e == e)
                this.cond.await();
            
        } finally {
            l.unlock();
        }

    }
    
}
