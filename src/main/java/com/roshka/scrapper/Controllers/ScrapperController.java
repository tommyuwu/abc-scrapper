package com.roshka.scrapper.Controllers;

import com.roshka.scrapper.Models.Noticia;
import com.roshka.scrapper.Services.ScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import com.roshka.scrapper.Exceptions.CustomExceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    private final ScrapperService scrapperService;

    @Autowired
    public ScrapperController(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @GetMapping("/consulta")
    public ResponseEntity<List<Noticia>> consulta(@RequestParam("q") String query, @RequestParam("f") Boolean foto) {
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
