package com.digis01.MMateoProgramacionNCapas.restController;

import com.digis01.MMateoProgramacionNCapas.DAO.UsuarioJPADAOImplementacion;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioJPADAOImplementacion usuarioJPADAOImplementacion;


    @GetMapping
    public ResponseEntity GetAll() {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.GetAll();
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("{idUsuario}")
    public ResponseEntity GetById(@PathVariable("idUsuario") int idUsuario) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.GetById(idUsuario);
            ;
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping
    public ResponseEntity Add(@RequestPart("usuario") UsuarioJPA usuario, @RequestParam("imagenFile") MultipartFile multipartFile) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.Add(usuario, multipartFile);
            // result.status = 201;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PutMapping("{idUsuario}")
    public ResponseEntity Update(@RequestBody UsuarioJPA usuario, @PathVariable("idUsuario") int idUsuario) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.Update(usuario, idUsuario);
            // result.status = 201;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PatchMapping("imagen/{idUsuario}")
    public ResponseEntity UpdateImagen(@PathVariable("idUsuario") int idUsuario, @RequestParam("imagenFile") MultipartFile file) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.UpdateImagen(idUsuario, file);
            // result.status = 201;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @DeleteMapping("{idUsuario}")
    public ResponseEntity Delete(@PathVariable("idUsuario") int idUsuario) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.Delete(idUsuario);
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping("search")
    public ResponseEntity GetAllDinamico(@RequestBody UsuarioJPA usuario) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.GetAllDinamico(usuario);
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping("cargaMasivaValidar")
    public ResponseEntity ValidarCarga(@RequestParam("file") MultipartFile file) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.ValidarCarga(file);
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping("cargaMasivaProcesar")
    public ResponseEntity ProcesarCarga(HttpServletRequest request) {
        Result result = new Result();
        try {
            String header = request.getHeader("Authorization");
            String token = header.substring(7);
            if (token != null) {
                result = usuarioJPADAOImplementacion.ProcesarCarga(token);
            } else {
                result.correct = false;
                result.errorMessage = "Falta token";
                result.status = 401;
            }
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PatchMapping("{idUsuario}/bajalogica")
    public ResponseEntity BajaLogica(@PathVariable("idUsuario") int idUsuario, @RequestBody boolean status) {
        Result result = new Result();
        try {
            result = usuarioJPADAOImplementacion.UpdateStatus(idUsuario, status);
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return ResponseEntity.status(result.status).body(result);
    }

}
