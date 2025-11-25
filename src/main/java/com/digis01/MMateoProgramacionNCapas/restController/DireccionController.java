package com.digis01.MMateoProgramacionNCapas.restController;

import com.digis01.MMateoProgramacionNCapas.DAO.DireccionJPADAOImplementacion;
import com.digis01.MMateoProgramacionNCapas.JPA.DireccionJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/direccion")
public class DireccionController {

    @Autowired
    private DireccionJPADAOImplementacion direccionJPADAOImplementacion;

    @GetMapping("{idDireccion}")
    public ResponseEntity GetById(@PathVariable("idDireccion") int idDireccion) {
        Result result = new Result();
        try {
            result = direccionJPADAOImplementacion.GetById(idDireccion);
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

    @PostMapping("{idUsuario}")
    public ResponseEntity Add(@RequestBody DireccionJPA direccionJPA, @PathVariable("idUsuario") int idDireccion) {
        Result result = new Result();
        try {
            result = direccionJPADAOImplementacion.AddByIdUsario(direccionJPA, idDireccion);
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

    @PutMapping("{idUsuario}")
    public ResponseEntity Update(@RequestBody DireccionJPA direccionJPA, @PathVariable("idUsuario") int idDireccion) {
        Result result = new Result();
        try {
            result = direccionJPADAOImplementacion.Update(direccionJPA, idDireccion);
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

    @DeleteMapping("{idDireccion}")
    public ResponseEntity Delete(@PathVariable("idDireccion") int idDireccion) {
        Result result = new Result();
        try {
            result = direccionJPADAOImplementacion.Delete(idDireccion);
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
}
