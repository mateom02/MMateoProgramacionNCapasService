package com.digis01.MMateoProgramacionNCapas.DAO;

import com.digis01.MMateoProgramacionNCapas.JPA.EstadoJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
public class EstadoJPADAOImplementacion implements IEstadoDAOJPA {

    @Autowired
    private EntityManager entityManager;

    @CrossOrigin(origins = "http://localhost:8081")
    @Override
    public Result GetByIdPais(int idPais) {
        Result result = new Result();

        try {
            TypedQuery queryEstado = entityManager.createQuery("SELECT e FROM EstadoJPA e WHERE e.Pais.IdPais = :idPais", EstadoJPA.class).setParameter("idPais", idPais);
            List<EstadoJPA> estados = queryEstado.getResultList();
            result.object = estados;
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

}
