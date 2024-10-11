/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.jcs;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;

public class FechaUtils {
    
    public static final String dateFormat = "dd/MM/yyyy HH:mm";

    // Función para convertir LocalDateTime o Date a String con formato "día/mes/año hora:min"
    public static String fechaToString(Object fecha) {
        String formattedDate = "";
        
        // Verifica si el objeto es de tipo LocalDateTime
        if (fecha instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            formattedDate = ((LocalDateTime) fecha).format(formatter);
        }
        // Verifica si el objeto es de tipo Date
        else if (fecha instanceof Date) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            formattedDate = formatter.format((Date) fecha);
        } else {
            throw new IllegalArgumentException("El tipo de fecha no es soportado." + fecha);
        }
        
        return formattedDate;
    }
    
     public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    public static void main(String[] args) {
        // Prueba con LocalDateTime
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        System.out.println("LocalDateTime: " + fechaToString(nowLocalDateTime));
        
        // Prueba con Date
        Date nowDate = new Date();
        System.out.println("Date: " + fechaToString(nowDate));
    }
    
     // Función para obtener la fecha de hoy como un objeto Date
    public static Date obtenerFechaDeHoy() {
        return new Date();
    }
    
    
    // Función para convertir String a Date con formato "día/mes/año hora:min"
    public static Date stringToDate(String fechaStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(fechaStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // O lanza una excepción, dependiendo de cómo quieras manejar el error
        }
    }


}
