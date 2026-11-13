package service;
import java.util.List;
public class TransactionProcessor {
    public void process(List<Runnable> tasks) { for (Runnable task : tasks) task.run(); }
}
