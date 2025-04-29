package cr.ac.una.agrow.service;

import java.util.List;

public interface CRUD<T> {

    public void save(T t);
    public void delete(T t);
    public List<T> getAll();
    public T getById(int i);
}
