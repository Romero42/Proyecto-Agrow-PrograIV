/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.jpa.HarvestRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author miste
 */
@Service
public class HarvestService implements CRUD<Harvest> {

    @Autowired
    private HarvestRepository harvestRepository;

    @Override
    public boolean save(Harvest harvest) {
        try {
            harvestRepository.save(harvest);
            return true;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(Harvest harvest) {
        try {
            harvestRepository.delete(harvest);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            // Puedes agregar lógica adicional si deseas manejar el error
        }
    }

    @Override
    public List<Harvest> getAll() {
        return harvestRepository.findAll();
    }

    @Override
    public Harvest getById(int id) {
        return harvestRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<Harvest> getHarvestByQuality(String quality) {
        return harvestRepository.getByQuality(quality);
    }

    @Transactional
    public List<Harvest> getHarvestByDestiny(String destiny) {
        return harvestRepository.getByDestiny(destiny);
    }

    // New method for paged content
    public Page<Harvest> getAllPaged(Pageable pageable) {
        return harvestRepository.findAll(pageable);
    }

    // New method with pagination
    @Transactional
    public Page<Harvest> getHarvestByQualityPaged(String quality, Pageable pageable) {
        return harvestRepository.getByQualityPaged(quality, pageable);
    }

    // New method with pagination
    @Transactional
    public Page<Harvest> getHarvestByDestinyPaged(String destiny, Pageable pageable) {
        return harvestRepository.getByDestinyPaged(destiny, pageable);
    }

}
