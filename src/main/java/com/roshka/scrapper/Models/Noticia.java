package com.roshka.scrapper.Models;

import lombok.Data;

@Data
public class Noticia {
    private String fecha;
    private String enlace;
    private String enlaceFoto;
    private String titulo;
    private String resumen;
    private String contenidoFoto;
    private String contenTypeFoto;
}
