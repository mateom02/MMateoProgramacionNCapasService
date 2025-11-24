package com.digis01.MMateoProgramacionNCapas.DAO;

import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.RolJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.ValidacionResponse;
import com.digis01.MMateoProgramacionNCapas.Service.JwtService;
import com.digis01.MMateoProgramacionNCapas.Service.ValidacionService;
import com.digis01.MMateoProgramacionNCapas.exception.ResourceAlreadyExistsException;
import com.digis01.MMateoProgramacionNCapas.exception.ResourceNotFoundException;
import com.digis01.MMateoProgramacionNCapas.restController.ErrorCarga;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Repository
public class UsuarioJPADAOImplementacion implements IUsuarioJPA {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ValidacionService validacionService;

    @Autowired
    private JwtService jwtService;

    @Override
    public Result GetAll() {
        Result result = new Result();
        try {

            TypedQuery queryUsuario = entityManager.createQuery("FROM UsuarioJPA ORDER BY IdUsuario", UsuarioJPA.class);
            List<UsuarioJPA> usuarios = queryUsuario.getResultList();
            result.object = usuarios;
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    public Result GetById(int idUsuario) {
        Result result = new Result();
        try {
            UsuarioJPA usuariojpa = entityManager.find(UsuarioJPA.class, idUsuario);
            if (usuariojpa == null) {
                throw new ResourceNotFoundException("Usuario con id: " + idUsuario + " no encontrado");
            }
            result.object = usuariojpa;
            result.correct = true;
            result.status = 200;
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return result;
    }

    @Override
    @Transactional
    public Result Add(UsuarioJPA usuario, MultipartFile imagenFile) {
        Result result = new Result();

        //Validar que el email enviado no exista en la base de datos
        TypedQuery queryEmailUsuario = entityManager.createQuery("FROM UsuarioJPA u WHERE u.Email = :email", UsuarioJPA.class)
                .setParameter("email", usuario.getEmail());

        List<UsuarioJPA> resultsEmail = queryEmailUsuario.getResultList();

        if (!resultsEmail.isEmpty()) {
            throw new ResourceAlreadyExistsException("El email " + usuario.getEmail() + " ya existe en la base de datos");
        }

        //Validar imagen
        if (imagenFile != null) {
            try {
                //vuelvo a asegurarme que es jpg o png
                String extension = imagenFile.getOriginalFilename().split("\\.")[1];
                if (extension.equals("jpg") || extension.equals("png")) {
                    byte[] byteImagen = imagenFile.getBytes();
                    String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
                    usuario.setImagen(imagenBase64);
                }
            } catch (IOException ex) {
                //Logger.getLogger(Usua.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        //Checar si direcciones existen
        if (usuario.Direcciones == null && usuario.Direcciones.isEmpty()) {
            entityManager.persist(usuario);
            result.correct = true;
            result.status = 201;

        } else {
            usuario.Direcciones.get(0).Usuario = usuario;
            entityManager.merge(usuario);
            result.correct = true;
            result.status = 201;
        }
        return result;
    }

    @Override
    @Transactional
    public Result Update(UsuarioJPA usuario, int idUsuario) {
        Result result = new Result();

        UsuarioJPA usuarioExistente = entityManager.find(UsuarioJPA.class, idUsuario);

//            //SettearValores
//            usuario.setPassword(usuarioExistente.getPassword());
//            usuario.setImagen(usuarioExistente.getImagen());
//            if (usuario.Direcciones.size() > 0) {
//                for (DireccionJPA direccionjpa : usuarioExistente.Direcciones) {
//                    Direccion direccion = modelMapper.map(direccionjpa, Direccion.class);
//                    usuario.Direcciones.add(direccion);
//                }
//            }
        usuario.setImagen(usuarioExistente.getImagen());
        usuario.setPassword(usuarioExistente.getPassword());
        if (usuarioExistente.Direcciones != null) {
            usuario.Direcciones = usuarioExistente.Direcciones;
        }
        entityManager.merge(usuario);
        result.correct = true;
        result.status = 201;

        return result;

    }

    @Override
    @Transactional
    public Result UpdateImagen(int idUsuario, String imagen) {
        Result result = new Result();
        try {

            UsuarioJPA usuariojpa = entityManager.find(UsuarioJPA.class, idUsuario);

            usuariojpa.setImagen(imagen);

            entityManager.merge(usuariojpa);
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
    public Result Delete(int idUsuario) {
        Result result = new Result();
        try {
            UsuarioJPA usuariojpa = entityManager.find(UsuarioJPA.class, idUsuario);
            entityManager.remove(usuariojpa);
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    public Result GetAllDinamico(UsuarioJPA usuario) {
        Result result = new Result();
        try {
            String query = "SELECT u FROM UsuarioJPA u WHERE ";
            result.objects = new ArrayList<>();

            query += "LOWER(u.Nombre) LIKE  '%' || :nombre || '%' AND LOWER(u.ApellidoPaterno) LIKE '%' || :pApellidoPaterno || '%' AND LOWER(u.ApellidoMaterno) LIKE '%' || :pApellidoMaterno|| '%'";

            if (usuario.Rol.getIdRol() > 0) {
                query += " AND u.Rol.IdRol = :pIdRol";
            }

            TypedQuery<UsuarioJPA> queryUsuario = entityManager.createQuery(query, UsuarioJPA.class);

            queryUsuario.setParameter("nombre", usuario.getNombre().toLowerCase());
            queryUsuario.setParameter("pApellidoPaterno", usuario.getApellidoPaterno().toLowerCase());
            queryUsuario.setParameter("pApellidoMaterno", usuario.getApellidoMaterno().toLowerCase());

            if (usuario.Rol.getIdRol() > 0) {
                queryUsuario.setParameter("pIdRol", usuario.Rol.getIdRol());
            }
            List<UsuarioJPA> usuarios = queryUsuario.getResultList();
            result.object = usuarios;
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public Result SaveAll(List<UsuarioJPA> usuarios) {

        Result result = new Result();
        try {
            for (UsuarioJPA usuario : usuarios) {

                entityManager.persist(usuario);
            }
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
//            entityTransaction.rollback();
        }
        return result;
    }

    @Override
    public Result ValidarCarga(MultipartFile file) {
        Result result = new Result();
        String extension = file.getOriginalFilename().split("\\.")[1];
        String pathBase = System.getProperty("user.dir");
        String pathArchivo = "src/main/resources/archivosCarga";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
        String pathDefinitvo = pathBase + "/" + pathArchivo + "/" + fecha + file.getOriginalFilename();
        try {
            file.transferTo(new File(pathDefinitvo));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        try {
            List<UsuarioJPA> usuarios = LecturaArchivoXLSX(pathDefinitvo);
            List<ErrorCarga> errores = ValidarDatosArchivo(usuarios);
            //Si no hay errores
            //Generar token
            if (errores.isEmpty()) {
                ValidacionResponse validacionResponse = new ValidacionResponse();
                validacionResponse.nombreArchivo = fecha + file.getOriginalFilename();
                validacionResponse.token = jwtService.generateToken();
                validacionResponse.estatus = "VALIDO";
                validacionResponse.fecha = LocalDateTime.now();
                result.object = validacionResponse;
                result.correct = true;
                result.status= 200;
            }else{
                ValidacionResponse validacionResponse = new ValidacionResponse();
                validacionResponse.nombreArchivo = file.getOriginalFilename() + fecha;
                validacionResponse.token = null;
                validacionResponse.estatus = "ERROR";
                validacionResponse.fecha = LocalDateTime.now();
                validacionResponse.descripcion = "El archivo tuvo errores";
                result.object = validacionResponse;
                result.objects = errores.stream().map(errorCarga -> (Object) errorCarga).toList();
                result.correct = false;
                result.status= 200;
            }
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
    @Transactional
    public Result ProcesarCarga(String nombreArchivo) {
        Result result = new Result();
        try{
            String pathArchivo = "src/main/resources/archivosCarga"+"/" + nombreArchivo;
            List<UsuarioJPA> usuarios = LecturaArchivoXLSX(pathArchivo);
            for (UsuarioJPA usuario : usuarios) {
                entityManager.persist(usuario);
            }
            ValidacionResponse validacionResponse = new ValidacionResponse();
            validacionResponse.nombreArchivo =nombreArchivo;
            validacionResponse.token = null;
            validacionResponse.estatus = "PROCESADO";
            validacionResponse.fecha = LocalDateTime.now();
            validacionResponse.descripcion = "El archivo se procceso con exito";
            result.object = validacionResponse;
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.status =500;
            result.ex = ex;
        }
        return result;
    }

    public List<UsuarioJPA> LecturaArchivoXLSX(String pathDefinitvo) {
        List<UsuarioJPA> usuarios = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(pathDefinitvo)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                UsuarioJPA usuario = new UsuarioJPA();
                usuario.setUserName(row.getCell(0).toString().trim());
                usuario.setNombre(row.getCell(1).toString().trim());
                usuario.setApellidoPaterno(row.getCell(2).toString().trim());
                usuario.setApellidoMaterno(row.getCell(3).toString().trim());
                usuario.setEmail(row.getCell(4).toString().trim());
                usuario.setPassword(row.getCell(5).toString().trim());
                usuario.setFechaNacimiento(row.getCell(6).getDateCellValue());
                usuario.setSexo(row.getCell(7).toString().trim());
                DataFormatter formatter = new DataFormatter();
                Cell cellPhone = row.getCell(8);
                Cell cellCelular = row.getCell(9);
                String phone = formatter.formatCellValue(cellPhone);
                String celular = formatter.formatCellValue(cellCelular);
                usuario.setTelefono(phone);
                usuario.setCelular(celular);
                usuario.setCurp(row.getCell(10).toString().trim());
                usuario.Rol = new RolJPA();
                usuario.Rol.setIdRol((int) row.getCell(11).getNumericCellValue());
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            usuarios = null;
        }
        return usuarios;
    }

    public List<ErrorCarga> ValidarDatosArchivo(List<UsuarioJPA> usuarios) {
        List<ErrorCarga> errores = new ArrayList<>();
        int linea = 1;
        for (UsuarioJPA usuario : usuarios) {
            BindingResult bindingResult = validacionService.validateObject(usuario);
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                FieldError fieldError = (FieldError) error;
                ErrorCarga errorCarga = new ErrorCarga();
                errorCarga.campo = fieldError.getField();
                errorCarga.descripcion = fieldError.getDefaultMessage();
                errorCarga.linea = linea;
                errores.add(errorCarga);
            }
            linea++;
        }
        return errores;
    }

}
