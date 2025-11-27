
package com.digis01.MMateoProgramacionNCapas.DAO;

import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUsuarioJPA {
    Result GetAll();
    Result Add(UsuarioJPA usuario, MultipartFile file);
    Result Update(UsuarioJPA usuario, int idUsuario);
    Result UpdateImagen(int idUsuario, MultipartFile fiel);
    Result GetAllDinamico(UsuarioJPA usuario);
    Result GetById(int idUsuario);
    Result Delete(int idUsuario);
    Result ValidarCarga(MultipartFile file);
    Result ProcesarCarga(String token);
    Result SaveAll(List<UsuarioJPA> usuarios);
    Result UpdateStatus(int idUsuario, boolean status);
}
