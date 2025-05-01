package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.jpa.machineryR_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class machineryR_Service implements CRUD<machineryRental>{

    @Autowired
    private machineryR_repository repo;


    @Override
    public boolean save(machineryRental machineryRental) {
        try {
            repo.save(machineryRental);
            return true;
        } catch (DataAccessException ex) {
            return false;
        }

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

            return false;
        }
    }

    @Override
    public List<machineryRental> getAll() {
        return repo.findAll();
    }

    @Override
    public machineryRental getById(int id) {
        return repo.findById(id).orElse(null);
    }

    public boolean update(machineryRental machineryRental){

        return save(machineryRental);
    }

}
