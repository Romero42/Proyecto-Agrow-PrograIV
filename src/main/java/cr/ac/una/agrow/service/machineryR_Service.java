package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.jpa.machineryR_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class machineryR_Service implements CRUD<machineryRental>{

    @Autowired
    private machineryR_repository repo;


    @Override
    public void save(machineryRental machineryRental) {

    }

    @Override
    public void delete(machineryRental machineryRental) {

    }

    public boolean deleteById(int id) {
        if (!repo.existsById(id)) {
            return false;
        }
        try {
            repo.deleteById(id);
            return true;
        } catch (Exception e) {
            // loguealo si querés: e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<machineryRental> getAll() {
        return repo.findAll();
    }

    @Override
    public machineryRental getById(int i) {
        return null;
    }
}
