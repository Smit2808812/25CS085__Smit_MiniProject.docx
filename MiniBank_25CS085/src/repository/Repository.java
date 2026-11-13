package repository;
import java.util.ArrayList;
import java.util.List;
public class Repository<T> {
    private final ArrayList<T> items = new ArrayList<>();
    public void add(T item){ items.add(item); }
    public ArrayList<T> all(){ return new ArrayList<>(items); }
    public void clear(){ items.clear(); }
    public void addAll(List<T> values){ items.addAll(values); }
}
