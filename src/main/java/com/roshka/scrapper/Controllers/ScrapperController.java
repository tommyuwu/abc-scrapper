package com.roshka.scrapper.Controllers;

import com.roshka.scrapper.Models.Noticia;
import com.roshka.scrapper.Services.ScrapperService;
import com.roshka.scrapper.Utils.JwtUtil;
import com.roshka.scrapper.Exceptions.CustomExceptions.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    private final ScrapperService scrapperService;

    public ScrapperController(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @GetMapping("/token")
    public String getToken() {
        String subject = "usuario";
        return JwtUtil.generateToken(subject);
    }

    @GetMapping("/consulta")
    @SecurityRequirement(name = "jwt")
    public ResponseEntity<List<Noticia>> consulta(@RequestHeader("Authorization") String token, @RequestParam("q") String query, @RequestParam("f") Boolean foto) {
        JwtUtil.validateTokenAndGetSubject(token.substring("Bearer ".length()));
        try {
            if (query.isBlank()) {
                throw new BadRequestException("Parámetros inválidos");
            }
            List<Noticia> response = scrapperService.obtenerPagina(query, foto);
            if (response.isEmpty()) {
                throw new NotFoundException("No se encuentran noticias para el texto: " + query);
            }
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            throw new NotFoundException("No se encuentran noticias para el texto: " + query);
        } catch (BadRequestException ex) {
            throw new BadRequestException("Parámetros inválidos");
        } catch (Exception ex) {
            throw new InternalServerException("Error interno del servidor");
        }
    }
}
