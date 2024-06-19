package services;

import java.util.List;

public interface IServicea<T> {
    void add(T t);
    void delete(int id);
    void update(T t,int id);
    List<T> getAll();
    T getById(int id);

}
