/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digis01.MMateoProgramacionNCapas.jwtutils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class JwtCacheService {

    private final Map<String, Integer> cacheTokens = new ConcurrentHashMap<>();

    public boolean RegistrarUso(String token) {
        Integer usos = cacheTokens.get(token);
        boolean valido = false;
        if (usos == null) {
            cacheTokens.put(token, 1);
            valido = true;
        } else if (usos == 5) {
            valido = false;
        } else {
            cacheTokens.put(token, usos + 1);
            valido = true;
        }
        return  valido;
    }

}
