package services;



import java.sql.SQLException;
import java.util.List;

public interface IServiceG<T> {


    void Add(T entity);


    public List<T> getAll() throws SQLException;

    public boolean deleteById(int id);
    public T getById(int id);
    public void update(T entity);
}
