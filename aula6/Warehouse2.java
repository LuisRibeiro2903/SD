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
      p.cond.signalAll();
    } finally {
      l.unlock();
    }
  }


  public Product test (List<Product> lp)
  {
    for (Product p : lp)
    {
      if (p.quantity == 0)
        return p;
    }
    return null;
  }

  public void consume(Set<String> items) throws InterruptedException{
    
    l.lock();
    try{
      List<Product> lp = items.stream().map((e) -> get(e)).toList();
      Product p;
      while ((p = test(lp)) != null)
        p.cond.await();
      for(Product e : lp)
        e.quantity--;  
    } finally {
      l.unlock();
    }
  }
}
