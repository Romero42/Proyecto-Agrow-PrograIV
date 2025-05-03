package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.jpa.machineryR_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class machineryR_Service implements CRUD<machineryRental>{

    @Autowired
    private machineryR_repository repo;


    //guarda un alquiler y valida si se guardo
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

    //carga una lista con los datos de la bd
    @Override
    public List<machineryRental> getAll() {
        return repo.findAll();
    }

    //retorna un alquiler que se obtiene por id
    @Override
    public machineryRental getById(int id) {
        return repo.findById(id).orElse(null);
    }

    //Actualiza un alquiler en la bd y valida que se realizo el cambio
    public boolean update(machineryRental machineryRental){

        return save(machineryRental);
    }

    //carga una pagina con alquiler que esten en las fechas inicial y final
    public Page<machineryRental> dateRange(LocalDate start, LocalDate end, Pageable pageable) {

        return repo.findByRentStartDayBetween(start, end, pageable);
    }

    //carga una pagina con los alquileres de una fecha inicial que se pase
    public Page<machineryRental> dateRangeByStart(LocalDate start, Pageable pageable) {

        return repo.findByRentStartDayGreaterThanEqual(start, pageable);
    }

    //carga una pagina con todos los alquiles de una fecha antes de la final que se pase
    public Page<machineryRental> dateRangeByFinal(LocalDate end, Pageable pageable) {

        return repo.findByRentFinalDayLessThanEqual(end, pageable);
    }

    //retorna un alquiler por id de maquina
    public machineryRental findByIdMaquina(Integer id_maquina) {
        return repo.findFirstByIdMaquina(id_maquina).orElse(null);
    }

    //carga una pagina con los alquilers
    public Page<machineryRental> getAllPaginated(Pageable pageable) {
        return repo.findAll(pageable);
    }


}
