import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank{

  private static class Account {
    private long balance;
    Account(long balance) { this.balance = balance; }
    long balance() { return balance; }
    boolean deposit(long value) {
      balance += value;
      return true;
    }
  }

  Lock l = new ReentrantLock();

  // Our single account, for now
  private Account savings = new Account(0);

  // Account balance
  public long balance() {
    return savings.balance();
  }

  // Deposit
  boolean deposit(long value) {
    l.lock();
    try
    {
      return savings.deposit(value);
    }
    finally
    {
      l.unlock();
    }
  }
}

class Depositos implements Runnable 
{
  long depositos;
  long valor;
  Bank conta;

  Depositos(long d, long v, Bank conta)
  {
    this.depositos = d;
    this.valor = v;
    this.conta = conta;
  }

  public void run ()
  {
    for(long i = 0; i < this.depositos; i++)
    {
      this.conta.deposit(this.valor);
    }
  }
}

class Main 
{
  public static void main (String[] args) throws InterruptedException
  {
    int N = Integer.parseInt(args[0]);
    int d = Integer.parseInt(args[1]);
    int v = Integer.parseInt(args[2]);
    Bank b = new Bank();

    Thread[] t = new Thread[N];
    for(int i = 0; i < N; i++)
      t[i] = new Thread(new Depositos(d, v, b));

    for(int i = 0; i < N; i++)
      t[i].start();

    for(int i = 0; i < N; i++)
      t[i].join();

    System.out.println(b.balance());
  }
}
