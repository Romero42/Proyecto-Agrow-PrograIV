/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.service.harvest;

import java.util.List;

/**
 *
 * @author miste
 */

//Definimos una coleccion generica
//osea puede ingresar cualquier tipo de dato
//le puedo pasar cualquier objeto que este mapeado en la bd y va para la tabla que es
public interface CRUD<T> {
    public void save(T t);
    public void delete(int i);
    public List<T> getAll();
    public T getById(int i);
}
