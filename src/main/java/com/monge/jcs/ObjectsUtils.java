/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.jcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Annotation;
import java.util.ArrayList;



/**
 *
 * @author HP
 */
public class ObjectsUtils {
    // Método genérico para obtener el valor del primer campo declarado de un objeto
    public static <T> Object getFirstFieldValue(T obj) {
        if (obj == null) {
            System.out.println("El objeto proporcionado es nulo.");
            return null;
        }
// Obtener la clase del objeto
        Class clazz = obj.getClass();
             System.out.println("tipo "+clazz.getName());
        try {
            // Obtener todos los campos declarados
            Field[] fields = clazz.getDeclaredFields();

            // Verificar que haya al menos un campo
            if (fields.length > 0) {
                Field firstField = fields[0]; // Obtener el primer campo
                firstField.setAccessible(true); // Habilitar acceso a campos privados
                return firstField.get(obj); // Obtener el valor del primer campo
            } else {
                System.out.println("No hay campos declarados en la clase: " + clazz.getName());
                return null;
            }
            
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            System.out.println("No se puede acceder al campo: " + e.getMessage());
            return null;
        }catch(java.lang.reflect.InaccessibleObjectException e){
             System.out.println(clazz.getClass().getName());
                    e.printStackTrace();
            return null;
                    }
    }
    
    /***
     * 
     * @param clazz
     * @return plural name for table example dog -> dogs
     */
    public static String getClassNameForSQLTable(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase() + "s";
    }
    
       // Método para obtener el nombre del primer campo declarado de un objeto
    public static <T> String getFirstFieldName(T clazz) {
        try {
           

            // Obtener todos los campos declarados
            Field[] campos = clazz.getClass().getDeclaredFields();
            
            
              // Recorrer los campos para encontrar el primero que no sea una anotación
            for (Field campo : campos) {
                // Verificar si el campo no es una anotación
               if (!Annotation.class.isAssignableFrom(campo.getType())) {
                    return campo.getName(); // Devolver el nombre del primer campo
               }
            }

           
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    
        public static String arrToJsonString(Object o) {
   
          // Obtener el tipo de la lista usando TypeToken
        Type listType = new TypeToken<Object>() {}.getType();
         // Crear Gson con formato pretty
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  // Activar formato pretty
                .create();
        
        // Convertir el objeto a JSON
        return gson.toJson(o,listType);
    }
    
    public static String toJsonString(Object o) {
   
         // Crear Gson con formato pretty
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  // Activar formato pretty
                .create();
        
        // Convertir el objeto a JSON
        return gson.toJson(o);
    }
    
 
     // Método genérico para convertir JSON a un ArrayList de objetos genéricos
    public static <T> ArrayList<T> fromJsonArray(String jsonString, Class<T> clazz) {

        System.out.println("loading array "+jsonString);
         return new Gson().fromJson(jsonString, new TypeToken<ArrayList<T>>(){}.getType());
    }
    
       // Método genérico para mapear un ResultSet a un objeto
    public static <T> T mapResultSetToObject(ResultSet resultSet, Class<T> clazz){
        T instance;
        try {
            // Crear una nueva instancia del objeto
            instance = clazz.getDeclaredConstructor().newInstance();

            // Asignar los valores de la base de datos a los campos del objeto usando reflexión
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // Permitir acceso a campos privados
                Object columnValue = resultSet.getObject(field.getName()); // Obtener el valor de la columna

                if (columnValue != null) {
                    // Verificar el tipo y asignar el valor apropiadamente
                    if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(instance, resultSet.getInt(field.getName()));
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        field.set(instance, resultSet.getDouble(field.getName()));
                    } else if (field.getType() == String.class) {
                        field.set(instance, resultSet.getString(field.getName()));
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.set(instance, ((Number) columnValue).intValue() != 0); // 0 es false, cualquier otro número es true
                    } else if (field.getType() == java.util.Date.class) {
                        field.set(instance,FechaUtils.stringToDate(resultSet.getString(field.getName())));
                    }else if (field.getType() == java.util.ArrayList.class) {
                        field.set(instance,fromJsonArray(resultSet.getString(field.getName()),java.util.ArrayList.class));
                    }
                    // Agregar más tipos si es necesario
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException e) {
            throw new RuntimeException("Error al mapear el ResultSet a objeto: " + e.getMessage(), e);
        }
        return instance;
    }
    
      public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
