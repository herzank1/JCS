/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.jcs;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author HP
 */
public class General {
    
       public static String stringToSHA256(String input) {
        try {
            // Obtener una instancia de MessageDigest con el algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Aplicar SHA-256 a la cadena de entrada y obtener el hash como array de bytes
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convertir los bytes a formato hexadecimal
            return bytesToHex(encodedhash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Método auxiliar para convertir un array de bytes en una cadena hexadecimal
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
}
