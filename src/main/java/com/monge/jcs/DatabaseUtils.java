/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.jcs;


import static com.monge.jcs.DataBase.getConnection;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtils {

    public static String getTabla(Class<?> clazz) {

        try {
            crearTablaSiNoExiste(clazz);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obtenerNombrePlural(clazz);
    }

    // Método para crear la tabla si no existe
    public static void crearTablaSiNoExiste(Class<?> clazz) throws SQLException {
        String nombreTabla = clazz.getSimpleName().toLowerCase() + "s"; // Nombre de la tabla en plural
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + nombreTabla + " (");

        Field[] campos = clazz.getDeclaredFields(); // Obtener los campos de la clase

        for (Field campo : campos) {
            String nombreColumna = campo.getName();
            String tipoColumna;

            // Determinar el tipo de columna basado en el tipo de dato
            if (campo.getType() == String.class) {
                tipoColumna = "TEXT";
            } else if (campo.getType() == int.class || campo.getType() == Integer.class) {
                tipoColumna = "INTEGER";
            } else if (campo.getType() == double.class || campo.getType() == Double.class) {
                tipoColumna = "REAL";
            } else if (campo.getType() == java.util.Date.class) {
                tipoColumna = "TEXT";

                /*si el objecto es array list, crear columna tipo texto
                usar gson o json para su almacenamiento*/
            } else if (campo.getType().equals(java.util.ArrayList.class)) {
                tipoColumna = "TEXT";
            } else {
                tipoColumna = "BLOB"; // Para tipos no especificados
            }

            sql.append(nombreColumna).append(" ").append(tipoColumna).append(", ");
        }

        // Eliminar la última coma y espacio, y cerrar la declaración
        sql.setLength(sql.length() - 2); // Eliminar la última coma
        sql.append(")");

        try (Statement stmt = DataBase.getConnection().createStatement()) {
            stmt.execute(sql.toString()); // Ejecutar la creación de la tabla
            System.out.println("Tabla " + nombreTabla + " creada o ya existente.");

        }

    }

    public static boolean insertObjectoToTableInSql(Object o) {
        try {
            
            crearTablaSiNoExiste(o.getClass());
            
            ArrayList<Object> objFields = splitObjectFieldsToArrayListOfObjects(o);
            /*Generamos sql con parametros vacios*/
            String sql = generarInsertInto(obtenerNombrePlural(o.getClass()), objFields.size());
            /*creamos el PreparedStatement y llenamos los parametros con los campos del objecto*/
            PreparedStatement stm = DataBase.getConnection().prepareStatement(sql);
            prepararInsertInto(stm, o);
            stm.execute();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    // Método para obtener un objeto de la tabla especificada utilizando un campo como ID
    public static <T> T getObjectFromDb(T clazz, String id) {

        String tabla = DatabaseUtils.getTabla(clazz.getClass());
        String nombrePrimerCampo = ObjectsUtils.getFirstFieldName(clazz);

        String sql = "SELECT * FROM " + tabla + " WHERE " + nombrePrimerCampo + " = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            DatabaseUtils.psSetValue(pstmt, id, 1);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crear una instancia del objeto
                Object objeto = ObjectsUtils.mapResultSetToObject(rs, clazz.getClass());

                return (T) objeto; // Devolver el objeto creado
            } else {
                System.out.println("No se encontró el objeto "+clazz.getClass().getSimpleName()+" con ID: " + id
                +" en la tabla "+tabla);
                return null; // No se encontró el objeto
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Devolver null en caso de error
        }
    }

    public static boolean updateObjectoInTableInSql(Object o) {
        try {
            String firstField = ObjectsUtils.getFirstFieldName(o.getClass());
            String sql = generarUpdateWhereStatement(o.getClass(), firstField);
            PreparedStatement stm = DataBase.getConnection().prepareStatement(sql);
            prepararUpdateWhereStatement(stm, o, firstField);
            stm.execute();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    // Método para pluralizar el nombre de la clase
    public static String obtenerNombrePlural(Class<?> clazz) {
        String nombre = clazz.getSimpleName().toLowerCase(); // Obtener el nombre de la clase en minúsculas
        // Agregar "s" para formar el plural
        return nombre + "s";
    }

    // Función para convertir las variables de un objeto a un ArrayList
    public static ArrayList<Object> splitObjectFieldsToArrayListOfObjects(Object objeto) {
        ArrayList<Object> lista = new ArrayList<>();
        Field[] campos = objeto.getClass().getDeclaredFields(); // Obtener campos de la clase
        System.out.println(objeto.toString());
        for (Field campo : campos) {
            campo.setAccessible(true); // Permitir el acceso a campos privados
            try {
                lista.add(campo.get(objeto)); // Agregar el valor del campo a la lista

            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Manejo de excepción si no se puede acceder al campo
            }
        }
        return lista;
    }

    // Generamos un sql INSERT INTO con parametros vacios ?,?,
    public static String generarInsertInto(String tabla, int params) {
        try {
            // Construir el comando SQL
            StringBuilder sql = new StringBuilder("INSERT INTO " + tabla + " VALUES (");
            for (int i = 0; i < params; i++) {
                sql.append("?");
                if (i < params - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");

            return sql.toString();

        } catch (Exception e) {
            return null;
        }

    }

    public static void prepararInsertInto(PreparedStatement pstmt, Object obj) {

        int index = addObjectFieldsToPrepareStatement(pstmt, obj);

    }

    // Método para generar la consulta SQL de actualización
    /**
     * *
     *
     * @param clazz
     * @param whereIDFieldName es el nombre del campo para identificar el objeto
     * en la db
     * @return
     */
    public static String generarUpdateWhereStatement(Class<?> clazz, String whereIDFieldName) {
        StringBuilder sql = new StringBuilder("UPDATE " + obtenerNombrePlural(clazz) + " SET ");

        Field[] campos = clazz.getDeclaredFields();
        for (Field campo : campos) {
            sql.append(campo.getName()).append(" = ?, ");
        }

        // Eliminar la última coma y espacio
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE " + whereIDFieldName + " = ?"); // Asumiendo que el campo de ID es "id"

        return sql.toString();
    }

    // Método para preparar el PreparedStatement, insertamos los valores de los parametros conforme al objecto
    public static void prepararUpdateWhereStatement(PreparedStatement pstmt, Object obj, String whereIDFieldName) {

        int index = addObjectFieldsToPrepareStatement(pstmt, obj);

        // Establecer el ID al final en WHERE (suponiendo que el campo se llama "id")
        Field idField;
        try {
            idField = obj.getClass().getDeclaredField(whereIDFieldName);
            idField.setAccessible(true);
            Object idValue = idField.get(obj);

            psSetValue(pstmt, idValue, index);

        } catch (NoSuchFieldException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DatabaseUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     * Agrega todos los campos del objeto como parametros a un preparestatement
     *
     * @param pstmt
     * @param obj
     * @return
     */
    private static int addObjectFieldsToPrepareStatement(PreparedStatement pstmt, Object obj) {
        try {
            Field[] campos = obj.getClass().getDeclaredFields();

            int index = 1;
            for (Field campo : campos) {
                campo.setAccessible(true); // Habilitar acceso a campos privados
                Object valor = campo.get(obj); // Obtener el valor del campo

                psSetValue(pstmt, valor, index);
                index += 1;
            }
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * *
     * Establece el valor de una columna de un preparestatement verificando los
     * tipos de datos
     *
     * @param pstmt
     * @param valor
     * @param index
     */
    public static void psSetValue(PreparedStatement pstmt, Object valor, int index) {

        try {
            // Manejar tipos de datos
            if (valor instanceof String) {
                pstmt.setString(index++, (String) valor);
            } else if (valor instanceof Integer) {
                pstmt.setInt(index++, (Integer) valor);
            } else if (valor instanceof Double) {
                pstmt.setDouble(index++, (Double) valor);
            } else if (valor instanceof java.util.Date) {
                java.sql.Date sqlDate = new java.sql.Date(((java.util.Date) valor).getTime());
                pstmt.setString(index++, FechaUtils.fechaToString(sqlDate));
            } else if (valor instanceof Boolean) {
                pstmt.setBoolean(index++, (Boolean) valor);
            } else if (valor instanceof java.util.ArrayList) {
                pstmt.setString(index++, ObjectsUtils.arrToJsonString(valor)); // Convertir ArrayList a JSON String
            } else {
                pstmt.setObject(index++, valor); // Valor genérico para otros tipos
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Función genérica para obtener resultados de la base de datos
    public static <T> ArrayList<T> selectFrom(Class<T> clazz,  String columnName, Object value) {
        ArrayList<T> results = new ArrayList<>();
        
        String tableName = ObjectsUtils.getClassNameForSQLTable(clazz);
        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";

        try {

            PreparedStatement statement = getConnection().prepareStatement(query);
            // Establecer el valor del parámetro en el statement (String o Double)
            if (value instanceof String) {
                statement.setString(1, (String) value);
            } else if (value instanceof Double) {
                statement.setDouble(1, (Double) value);
            }

            ResultSet resultSet = statement.executeQuery();

            // Iterar sobre los resultados
            while (resultSet.next()) {
                // Crear una nueva instancia del objeto
                T instance = ObjectsUtils.mapResultSetToObject(resultSet, clazz);
                // Añadir la instancia a la lista de resultados
                results.add(instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

}
