/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.harvest.Harvest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 *
 * @author miste
 */
public interface HarvestRepository extends JpaRepository<Harvest, Integer> {
    @Procedure(name = "Harvest.getByQuality")
    List<Harvest> getByQuality(@Param("harvestQuality") String quality);
    
    @Procedure(name = "Harvest.getByDestiny")
    List<Harvest> getByDestiny(@Param("destiny") String destiny);
    
    @Query("SELECT h FROM Harvest h WHERE h.quality = :quality")
    Page<Harvest> getByQualityPaged(@Param("quality") String quality, Pageable pageable);
    
    @Query("SELECT h FROM Harvest h WHERE h.destiny LIKE %:destiny%")
    Page<Harvest> getByDestinyPaged(@Param("destiny") String destiny, Pageable pageable);
}
