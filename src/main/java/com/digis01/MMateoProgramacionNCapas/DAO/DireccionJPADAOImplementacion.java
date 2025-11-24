package com.digis01.MMateoProgramacionNCapas.DAO;

import com.digis01.MMateoProgramacionNCapas.JPA.DireccionJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DireccionJPADAOImplementacion implements IDireccionJPADAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Result AddByIdUsario(DireccionJPA direccion, int idUsuario) {
        Result result = new Result();

        try {
            UsuarioJPA usuariojpa = entityManager.find(UsuarioJPA.class, idUsuario);
            direccion.Usuario = usuariojpa;
            entityManager.persist(direccion);
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    @Transactional
    public Result Update(DireccionJPA direccion, int idUsuario) {
        Result result = new Result();

        try {

            DireccionJPA direccionExistente = entityManager.find(DireccionJPA.class, direccion.getIdDireccion());
            UsuarioJPA usuarioDireccion = entityManager.find(UsuarioJPA.class, idUsuario);

            direccionExistente = direccion;
            direccionExistente.Usuario = usuarioDireccion;

            entityManager.merge(direccionExistente);
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

    @Override
    @Transactional
    public Result Delete(int idDireccion) {
        Result result = new Result();
        try {
            DireccionJPA direccionJPA = entityManager.find(DireccionJPA.class, idDireccion);
            entityManager.remove(direccionJPA);
            result.correct=true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

}
