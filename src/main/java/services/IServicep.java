package services;



import java.sql.SQLException;
import java.util.List;

public interface IServicep<T> {


    void Add(T entity)throws SQLException;


    public List<T> getAll() throws SQLException;

    public void deleteById(int id)throws SQLException;
    public T getById(int id)throws SQLException;
    public void update(T entity)throws SQLException;


}
