package cr.ac.una.agrow.service.requests;

import cr.ac.una.agrow.domain.requests.Requests;
import cr.ac.una.agrow.data.requests.RequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Requests getById(int i) {
        return repoRequests.getReferenceById(i);
    }
}
