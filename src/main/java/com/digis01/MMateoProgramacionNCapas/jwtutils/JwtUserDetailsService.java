/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digis01.MMateoProgramacionNCapas.jwtutils;

import com.digis01.MMateoProgramacionNCapas.DAO.IUsuarioRepository;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private IUsuarioRepository iUsuarioRepository;

    public JwtUserDetailsService(IUsuarioRepository iUsuarioRepository) {
        this.iUsuarioRepository = iUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioJPA usuarioJPA = iUsuarioRepository.findByUserName(username);

        return User.withUsername(usuarioJPA.getUserName())
                .password(usuarioJPA.getPassword())
                .roles(usuarioJPA.Rol.getNombre())
                .build();

    }

}
