/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.Machinery;
import cr.ac.una.agrow.jpa.MachineryRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author andre
 */
@Service
public class MachineryService {

    private static final Logger LOG = Logger.getLogger(MachineryService.class.getName());

    @Autowired
    private MachineryRepository machineryRepository;

    @Transactional
    public Machinery saveMachinery(Machinery machinery) {
        try {
            return machineryRepository.save(machinery);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al guardar la maquinaria", e);
            throw new RuntimeException("Error al guardar la maquinaria", e);
        }
    }

    @Transactional
    public Machinery saveMachineryDefinitivo(Machinery machinery) {
        try {
            return machineryRepository.save(machinery);

        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al guardar la maquinaria", e);
            throw new RuntimeException("Error al guardar la maquinaria: " + e.getMostSpecificCause().getMessage(), e);
        }
    }

    @Transactional
    public Machinery updateMachinery(Machinery machinery) {
        try {
            if (!machineryRepository.existsById(machinery.getId())) {
                throw new RuntimeException("Maquinaria no encontrada con ID: " + machinery.getId());
            }
            return machineryRepository.save(machinery);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al actualizar la maquinaria", e);
            throw new RuntimeException("Error al actualizar la maquinaria", e);
        }
    }

    @Transactional
    public boolean deleteMachinery(int id) {
        try {
            if (!machineryRepository.existsById(id)) {
                return false;
            }
            machineryRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al eliminar la maquinaria", e);
            throw new RuntimeException("Error al eliminar la maquinaria", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Machinery> getAllMachinery() {
        try {
            return machineryRepository.findAll();
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al obtener todas las maquinarias", e);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Machinery> getMachineryById(int id) {
        try {
            return machineryRepository.findById(id);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al buscar maquinaria por ID", e);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<Machinery> getByDisponibilidad(boolean disponibilidad) {
        try {
            return machineryRepository.findByDisponibilidad(disponibilidad);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al filtrar por disponibilidad", e);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<Machinery> getByNombre(String nombre) {
        try {
            return machineryRepository.findByNombreContainingIgnoreCase(nombre);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al filtrar por nombre", e);
            return Collections.emptyList();
        }
    }
}
