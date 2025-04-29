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

    @Override
    public List<machineryRental> getAll() {
        return repo.findAll();
    }

    @Override
    public machineryRental getById(int i) {
        return null;
    }
}
