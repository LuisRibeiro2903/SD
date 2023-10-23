import java.util.*;
import java.util.concurrent.locks.*;

class Warehouse {
  private Map<String, Product> map =  new HashMap<String, Product>();

  private ReentrantLock l = new ReentrantLock();

  private class Product { 
    Condition cond = l.newCondition();
    int quantity = 0; 
  }

  private Product get(String item) {
    Product p = map.get(item);
    if (p != null) return p;
    p = new Product();
    map.put(item, p);
    return p;
  }

  public void supply(String item, int quantity) {
    l.lock();
    try{
      Product p = get(item);
      p.quantity += quantity;
    } finally {
      l.unlock();
    }
  }

  // Errado se faltar algum produto...
  public void consume(Set<String> items) throws InterruptedException{
    l.lock();
    try{
      for (String s : items)
      {
        Product p = get(s);
        while (p.quantity == 0)
          p.cond.await();
        p.quantity--;
      }  
    } finally {
      l.unlock();
    }
  }
}
