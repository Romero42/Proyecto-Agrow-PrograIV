package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.jpa.Producer_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Producer_Service implements CRUD<Producer>{

    @Autowired
    private Producer_Repository repo;

    @Override
    public boolean save(Producer producer) {
        try {
            repo.save(producer);
            return true;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(Producer producer) {
        
    }

    //elimina un alquiler por id
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
    public List<Producer> getAll() {
        return List.of();
    }

    @Override
    public Producer getById(int id) {
        return repo.findById(id).orElse(null);
    }

    //retorna el total de datos en la tabla
    public long sizeTable(){
        return repo.count();
    }

    //retorna una pagina solicitada con los productores en la base
    public Page<Producer> findAll(Pageable page){

        return repo.findAll(page);
    }

    //retorna una pagina con los productores usando el filtro de ciudad
    public Page<Producer> findByCity(String city, Pageable page){

      return   repo.findByCity(city, page);
    }
}
