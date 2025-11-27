package com.digis01.MMateoProgramacionNCapas.DAO;

import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.RolJPA;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import com.digis01.MMateoProgramacionNCapas.Service.JwtService;
import com.digis01.MMateoProgramacionNCapas.Service.ValidacionService;
import com.digis01.MMateoProgramacionNCapas.exception.ResourceAlreadyExistsException;
import com.digis01.MMateoProgramacionNCapas.exception.ResourceNotFoundException;
import com.digis01.MMateoProgramacionNCapas.restController.ErrorCarga;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.BufferedReader;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellType;

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
    public Result UpdateImagen(int idUsuario, MultipartFile imagenFile) {
        Result result = new Result();
        try {
            UsuarioJPA usuariojpa = entityManager.find(UsuarioJPA.class, idUsuario);
            //Validar imagen
            if (imagenFile != null) {
                String extension = imagenFile.getOriginalFilename().split("\\.")[1];
                if (extension.equals("jpg") || extension.equals("png")) {
                    byte[] byteImagen = imagenFile.getBytes();
                    String imagenBase64 = Base64.getEncoder().encodeToString(byteImagen);
                    usuariojpa.setImagen(imagenBase64);
                }
            }

            entityManager.merge(usuariojpa);
            result.correct = true;
            result.status = 200;
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

            if (usuario.status != null) {
                query += " AND u.status = :status";
            }

            TypedQuery<UsuarioJPA> queryUsuario = entityManager.createQuery(query, UsuarioJPA.class);

            queryUsuario.setParameter("nombre", usuario.getNombre().toLowerCase());
            queryUsuario.setParameter("pApellidoPaterno", usuario.getApellidoPaterno().toLowerCase());
            queryUsuario.setParameter("pApellidoMaterno", usuario.getApellidoMaterno().toLowerCase());

            if (usuario.Rol.getIdRol() > 0) {
                queryUsuario.setParameter("pIdRol", usuario.Rol.getIdRol());
            }
            if (usuario.status != null) {
                queryUsuario.setParameter("status", usuario.status);//Numero?
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
        }
        return result;
    }

    @Override
    public Result ValidarCarga(MultipartFile file) {
        Result result = new Result();
        String extension = file.getOriginalFilename().split("\\.")[1];
        String pathBase = System.getProperty("user.dir");
        String pathArchivo = "src/main/resources/archivosCarga";
        String pathLog = pathBase + "/src/main/resources/LOG_cargaMasiva.txt";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS"));
        String pathDefinitvo = pathBase + "/" + pathArchivo + "/" + fecha + file.getOriginalFilename();

        try {
            file.transferTo(new File(pathDefinitvo));

            List<UsuarioJPA> usuarios = new ArrayList<>();
            List<ErrorCarga> errores = new ArrayList<>();

            if (extension.equals("xlsx")) {
                usuarios = LecturaArchivoXLSX(pathDefinitvo); //Error al abrir archivo
            } else if (extension.equals("txt")) {
                usuarios = LecturaArchivoTXT(pathDefinitvo);//error al abrir el archivo
            }
            errores = ValidarDatosArchivo(usuarios);

            if (errores.isEmpty()) {
                String nombreArchivo = fecha + file.getOriginalFilename();
                String token = jwtService.generateToken();
                escribirLog(pathLog, nombreArchivo, token, "VALIDO", LocalDateTime.now(), "El archivo no tuvo errores");
                result.object = token;
                result.correct = true;
                result.status = 200;
            } else {
                String nombreArchivo = fecha + file.getOriginalFilename();
                escribirLog(pathLog, nombreArchivo, null, "ERROR", LocalDateTime.now(), "El archivo  tuvo errores");
                result.objects = errores.stream().map(errorCarga -> (Object) errorCarga).toList();
                result.correct = false;
                result.status = 222;//Error o algo
            }
        } catch (IOException ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return result;
    }

    @Override
    @Transactional
    public Result ProcesarCarga(String token) {
        Result result = new Result();

        //Validar token 
        if (!jwtService.validateToken(token)) {
            result.correct = false;
            result.status = 401;
            result.errorMessage = "El token no es valido";
            return result;
        }

        //Abrir archivo de log
        String pathBase = System.getProperty("user.dir");
        String pathLog = pathBase + "/src/main/resources/LOG_cargaMasiva.txt";
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(pathLog);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String lineaActual = bufferedReader.readLine();
            String ultimaLinea = "";
            while ((lineaActual = bufferedReader.readLine()) != null) {
                ultimaLinea = lineaActual;
            }
            String lineas[] = ultimaLinea.split("\\|");

            if (lineas[1].equals(token)) {
                String pathArchivo = pathBase + "/src/main/resources/archivosCarga/" + lineas[0];
                List<UsuarioJPA> usuarios = LecturaArchivoXLSX(pathArchivo);
                for (UsuarioJPA usuario : usuarios) {
                    entityManager.persist(usuario);
                }
                escribirLog(pathLog, lineas[0], token, "PROCESADO", LocalDateTime.now(), "El archivo fue procesado correctamente");
                result.correct = true;
                result.status = 200;
            } else {
                escribirLog(pathLog, lineas[0], token, "ERROR", LocalDateTime.now(), "Los Tokens no coinciden");
                result.correct = false;
                result.status = 401;
            }
        } catch (IOException ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.status = 500;
            result.ex = ex;
        }
        return result;
    }

    public List<UsuarioJPA> LecturaArchivoXLSX(String pathDefinitvo) throws IOException {
        List<UsuarioJPA> usuarios = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(pathDefinitvo);

        XSSFSheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {

            UsuarioJPA usuario = new UsuarioJPA();
            usuario.setUserName(obtenerValorCelda(row.getCell(0)));
            usuario.setNombre(obtenerValorCelda(row.getCell(1)));
            usuario.setApellidoPaterno(obtenerValorCelda(row.getCell(2)));
            usuario.setApellidoMaterno(obtenerValorCelda(row.getCell(3)));
            usuario.setEmail(obtenerValorCelda(row.getCell(4)));
            usuario.setPassword(obtenerValorCelda(row.getCell(5)));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                usuario.setFechaNacimiento(simpleDateFormat.parse(obtenerValorCelda(row.getCell(6))));
            } catch (ParseException ex) {
                Logger.getLogger(UsuarioJPADAOImplementacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            usuario.setSexo(obtenerValorCelda(row.getCell(7)));
            usuario.setTelefono(obtenerValorCelda(row.getCell(8)));
            usuario.setCelular(obtenerValorCelda(row.getCell(9)));
            usuario.setCurp(obtenerValorCelda(row.getCell(10)));
            usuario.Rol = new RolJPA();
            usuario.Rol.setIdRol((int) row.getCell(11).getNumericCellValue());
            usuario.status = true;
            usuarios.add(usuario);
        }
        return usuarios;
    }

    public List<UsuarioJPA> LecturaArchivoTXT(String path) throws FileNotFoundException, IOException {

        List<UsuarioJPA> usuarios = new ArrayList<>();
        InputStream inputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String linea = "";

        while ((linea = bufferedReader.readLine()) != null) {
            //Extraer los datos
            String[] campos = linea.split("\\|");
            UsuarioJPA usuario = new UsuarioJPA();
            usuario.setUserName(campos[0]);
            usuario.setNombre(campos[1]);
            usuario.setApellidoPaterno(campos[2]);
            usuario.setApellidoMaterno(campos[3]);
            usuario.setEmail(campos[4]);
            usuario.setPassword(campos[5]);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String fechaIngresada = campos[6];
            try {
                usuario.setFechaNacimiento(formatter.parse(fechaIngresada));
            } catch (ParseException ex) {
                Logger.getLogger(UsuarioJPADAOImplementacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            usuario.setSexo(campos[7]);
            usuario.setTelefono(campos[8]);
            usuario.setCelular(campos[9]);
            usuario.setCurp(campos[10]);
            usuario.Rol = new RolJPA();
            usuario.Rol.setIdRol(Integer.parseInt(campos[11]));
            usuario.status = true;
            usuarios.add(usuario);

        }
        return usuarios;
    }

    private String obtenerValorCelda(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }

    private void escribirLog(String pathLog, String nombreArchivo, String token, String status, LocalDateTime date, String comentario) throws FileNotFoundException {
        File archivo = new File(pathLog);
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(archivo, true));
        printWriter.write(nombreArchivo + "|" + token + "|" + status + "|" + date + "|" + comentario + System.lineSeparator());
        printWriter.close();
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

    @Override
    @Transactional
    public Result UpdateStatus(int idUsuario, boolean status) {
        Result result = new Result();

        try {
            UsuarioJPA usuarioJPA = entityManager.find(UsuarioJPA.class,
                    idUsuario);
            if (usuarioJPA.status) {
                usuarioJPA.status = false;
            } else {
                usuarioJPA.status = true;
            }
            entityManager.merge(usuarioJPA);
            result.correct = true;
            result.status = 200;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }
        return result;
    }

}
