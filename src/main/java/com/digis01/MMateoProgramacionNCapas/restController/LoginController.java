package com.digis01.MMateoProgramacionNCapas.restController;

import com.digis01.MMateoProgramacionNCapas.DAO.IUsuarioRepository;
import com.digis01.MMateoProgramacionNCapas.JPA.Result;
import com.digis01.MMateoProgramacionNCapas.JPA.UsuarioJPA;
import com.digis01.MMateoProgramacionNCapas.jwtutils.JwtUserDetailsService;
import com.digis01.MMateoProgramacionNCapas.jwtutils.RequestModel;
import com.digis01.MMateoProgramacionNCapas.jwtutils.TokenManager;
import jakarta.persistence.PostLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class LoginController {

    private JwtUserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;
    private IUsuarioRepository iUsuarioRepository;

    public LoginController(JwtUserDetailsService userDetailsService, AuthenticationManager authenticationManager, TokenManager tokenManager, IUsuarioRepository iUsuarioRepository) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.iUsuarioRepository = iUsuarioRepository;
    }

    @PostMapping("login")
    public ResponseEntity Login(@RequestBody RequestModel requestModel) throws Exception {

        Result result = new Result();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestModel.getUsername(), requestModel.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(requestModel.getUsername());
            UsuarioJPA user = iUsuarioRepository.findByUserName(requestModel.getUsername());
            String jwtToken = tokenManager.generarToken(userDetails, user.getIdUsuario());
            result.object = jwtToken;
            result.status = 200;
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        return ResponseEntity.status(result.status).body(result);
    }

}
