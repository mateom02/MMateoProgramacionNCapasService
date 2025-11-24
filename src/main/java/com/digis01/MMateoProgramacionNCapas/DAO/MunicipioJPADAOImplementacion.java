package com.digis01.MMateoProgramacionNCapas.DAO;

import com.digis01.MMateoProgramacionNCapas.JPA.MunicipioJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
public class MunicipioJPADAOImplementacion implements IMunicipioJPADAO {

    @Autowired
    private EntityManager entityManager;

    @CrossOrigin(origins = "http://localhost:8081")
    @Override
    public Result GetByIdEstado(int idEstado) {
        Result result = new Result();

        try {
            TypedQuery queryMunicipio = entityManager.createQuery("FROM MunicipioJPA m WHERE m.Estado.IdEstado = :idEstado", MunicipioJPA.class).setParameter("idEstado", idEstado);
            List<MunicipioJPA> municipios = queryMunicipio.getResultList();
            result.object =  municipios;
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

}
