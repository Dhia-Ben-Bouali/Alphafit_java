package services;



import entite.Messagerie;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {


    void Add(T entity);


    public List<T> getAll() throws SQLException;

    public boolean deleteById(int id);
    public T getById(int id);



    public boolean update(T entity);


}
