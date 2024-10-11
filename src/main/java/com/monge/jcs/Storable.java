/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.jcs;

import java.util.ArrayList;

/**
 *
 * @author HP
 */
public abstract class Storable<T> {
    
    DataBase db = new DataBase();

    /***
     * 
     * @return boolean if insertion on database tables success
     */
    public boolean insertInto(){
        return db.insertDinamicObject(this);
    }
    
    /***
     * 
     * @return boolean if update is success
     */
    public  boolean update() {
        return db.updateDinamicObject(this);
    }
    
    public <T> ArrayList<T> select(Class clazz,String propertyName, String value) {
        return db.select(clazz, propertyName, value);
    }

    
    public Object getById(String id) {
        return  db.obtenerObjetoPorId(this, id);
    }
    
    
  
  
    
}
