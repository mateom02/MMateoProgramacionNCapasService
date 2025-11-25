package com.digis01.MMateoProgramacionNCapas.restController;

import com.digis01.MMateoProgramacionNCapas.DAO.UsuarioJPADAOImplementacion;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import com.digis01.MMateoProgramacionNCapas.Service.JwtService;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import jakarta.servlet.http.HttpServlet;
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

    @Autowired
    private JwtService jwtService;

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

    @CrossOrigin(origins = "http://localhost:8081")
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
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtService.validateToken(token)) {
                    usuarioJPADAOImplementacion.ProcesarCarga();
                } else {
                    result.correct = false;
                    result.status = 404;
                    result.errorMessage = "Verifique el token";
                }
            } else {
                result.correct = false;
                result.status = 404;
                result.errorMessage = "Verifique el token";
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
