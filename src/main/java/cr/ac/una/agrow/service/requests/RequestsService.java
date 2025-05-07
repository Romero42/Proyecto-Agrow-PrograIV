package cr.ac.una.agrow.service.requests;

import cr.ac.una.agrow.domain.requests.Requests;
import cr.ac.una.agrow.jpa.RequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestsService implements CRUD<Requests> {

    @Autowired
    private RequestsRepository repoRequests;

    @Override
    public void save(Requests t) {
        repoRequests.save(t);
    }

    @Override
    public void delete(int i) {
        repoRequests.deleteById(i);
    }

    @Override
    public List<Requests> getAll() {
        return repoRequests.findAll();
    }

    //paginación
    public Page<Requests> getAllPaginated(int page, int size) {
        return repoRequests.findAll(PageRequest.of(page, size));
    }

    @Override
    public Requests getById(int i) {
        return repoRequests.getReferenceById(i);
    }

    @Override
    public List<Requests> search(String searchTerm){
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getAll();
        }
        return repoRequests.findByNameOrTypeContainingIgnoreCase(searchTerm, Pageable.unpaged()).getContent();
    }

   @Override
   public Page<Requests> searchNamePaginated(String searchTerm, int page, int size){
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getAllPaginated(page, size);
        }
        return repoRequests.findByNameOrTypeContainingIgnoreCase(searchTerm, PageRequest.of(page, size));
    }
}
