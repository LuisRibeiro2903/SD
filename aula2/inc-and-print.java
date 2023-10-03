class Increment implements Runnable{

  final long n;

  Increment (long I) 
  {
    this.n = I;
  }

  public void run() {
    
    for (long i = 0; i < this.n; i++)
      System.out.println(i);
  }
}

class Main {
  public static void main (String[] args) throws InterruptedException {
    int N = Integer.parseInt(args[0]);
    int I = Integer.parseInt(args[1]);
    
    Thread[] t = new Thread[N];
    for(int i = 0; i < N; i++)
      t[i] = new Thread(new Increment(I));

    for(int i = 0; i < N; i++)
      t[i].start();

    for(int i = 0; i < N; i++)
      t[i].join();

    System.out.println("Fim");
  }
}
