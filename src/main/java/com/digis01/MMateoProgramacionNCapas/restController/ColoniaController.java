package com.digis01.MMateoProgramacionNCapas.restController;

import com.digis01.MMateoProgramacionNCapas.DAO.ColoniaJPADAOImplementacion;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/colonia")
public class ColoniaController {

    @Autowired
    private ColoniaJPADAOImplementacion coloniaJPADAOimplementacion;

    @GetMapping("{idMunicipio}")
    public ResponseEntity GetByIdMunicipio(@PathVariable("idMunicipio") int idMunicipio) {

        Result result = new Result();
        try {
            result = coloniaJPADAOimplementacion.GetByIdMunicipio(idMunicipio);
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

    @GetMapping("byCodigoPostal/{codigoPostal}")
    public ResponseEntity GetByCodigoPostal(@PathVariable("codigoPostal") String codigoPostal) {
        Result result = new Result();
        try {
            result = coloniaJPADAOimplementacion.GetByCodigoPostal(codigoPostal);
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
