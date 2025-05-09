package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.jpa.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class User_Service implements CRUD<User>{

    @Autowired
    private User_Repository repo;

    //guarda un usuario
    @Override
    public boolean save(User user) {
        try {
            repo.save(user);
            return true;
        } catch (DataAccessException ex) {
            return false;
        }
    }

    @Override
    public void delete(User user) {

    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    //obtiene una pagina por filtro de tipo
    public Page<User> getByType(String type, Pageable page){
     return  repo.findByType(type, page);
    }

    //obtiene una pagina de la bd
    public Page<User> getAllUser(Pageable page){
        return  repo.findAll(page);
    }

    //busca por id
    @Override
    public User getById(int i) {
        return repo.findById(i).orElse(null);
    }

    //busca por usuario
    public User getByUser(String user){
       return repo.findByUser(user).orElse(null);
    }

    //borra por id
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
}
