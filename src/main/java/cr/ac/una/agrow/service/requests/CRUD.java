package cr.ac.una.agrow.service.requests;

import cr.ac.una.agrow.domain.requests.Requests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CRUD<T>{

    public void save(T t);
    public void delete(int i);
    public List<T> getAll();
    public T getById(int i);
    public List<T> search(String searchTerm);
    public Page<Requests> searchNamePaginated(String searchTerm, int page, int size);
    public Page<Requests> getAllPaginated(int page, int size);
}