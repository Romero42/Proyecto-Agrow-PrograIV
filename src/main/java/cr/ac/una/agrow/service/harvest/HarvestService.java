/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.service.harvest;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.jpa.harvest.HarvestRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void save(Harvest t) {
        harvestRepository.save(t);
    }

    @Override
    public void delete(int id) {
        harvestRepository.deleteById(id);
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
}
