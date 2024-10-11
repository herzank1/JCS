/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.jcs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase {

    private static String fileName = "tpDb.sqlite3"; // Cambia la ruta según sea necesario

    public DataBase() {
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        DataBase.fileName = fileName;
    }

    
    
    public DataBase(String fileName) {
        this.fileName = fileName;
    }
    
    

    // Método para obtener la conexión a la base de datos
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:"+fileName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private void executeStatement(PreparedStatement stm) {

        try {
            stm.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean insertDinamicObject(Object o) {

           return DatabaseUtils.insertObjectoToTableInSql(o);
          
    }

    public boolean updateDinamicObject(Object o) {
        
        
        
        return DatabaseUtils.updateObjectoInTableInSql(o);
    }

    // Método para eliminar todas las tablas de la base de datos SQLite
    public static void eliminarTodasLasTablas(Connection conn) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombreTabla = rs.getString("name");
                String dropSql = "DROP TABLE IF EXISTS " + nombreTabla + ";";
                try (Statement dropStmt = conn.createStatement()) {
                    dropStmt.execute(dropSql);
                    System.out.println("Tabla eliminada: " + nombreTabla);
                } catch (SQLException e) {
                    System.out.println("Error al eliminar la tabla: " + nombreTabla + " - " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las tablas: " + e.getMessage());
        }
    }

    // Método para obtener un objeto de la tabla especificada utilizando un campo como ID
    public  <T> T obtenerObjetoPorId(T clazz, String id) {

        return DatabaseUtils.getObjectFromDb(clazz, id);
    }

    
    public  <T> ArrayList<T> select(Class<T> clazz, String propertyName, Object value) {
        return DatabaseUtils.selectFrom(clazz, propertyName, value);
    }


  
}
