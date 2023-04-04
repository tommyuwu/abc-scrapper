package com.roshka.scrapper.Services;

import com.roshka.scrapper.Models.Noticia;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScrapperService {

    public List<Noticia> obtenerPagina(String parametro, Boolean foto) {
        String url = "https://www.abc.com.py/buscador/?query=" + parametro;
        WebDriverManager wdm = WebDriverManager.chromedriver().browserInDocker();
        WebDriver driver = wdm.create();
        try {
            driver.get(url);
            Document document = Jsoup.parse(driver.getPageSource());
            Elements body = document.getElementsByClass("queryly_item_row");

            List<Noticia> noticias = new ArrayList<>();
            for (Element elemento : body) {
                Element link = elemento.selectFirst("a");
                assert link != null;

                Noticia noticia = new Noticia();
                noticia.setFecha(elemento.selectFirst("a div[style*=\"margin-bottom:10px;\"]").text());
                noticia.setEnlace("https://www.abc.com.py" + link.attr("href"));
                noticia.setEnlaceFoto("https://www.abc.com.py" + elemento.selectFirst("div.queryly_advanced_item_imagecontainer[style*=background-image]").attr("style").replaceAll(".*?url\\(['\"]?(.*?)['\"]?\\).*", "$1"));
                noticia.setTitulo(elemento.selectFirst(".queryly_item_title").text());
                noticia.setResumen(elemento.selectFirst(".queryly_item_description").text());
                if (foto) {
                    Noticia noticiaConFoto = traerFoto(noticia);
                    noticias.add(noticiaConFoto);
                } else {
                    noticias.add(noticia);
                }
            }
            return noticias;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        } finally {
            driver.quit();
            wdm.quit();
        }
    }

    public Noticia traerFoto(Noticia noticia){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(noticia.getEnlaceFoto(), HttpMethod.GET, entity, byte[].class);

        noticia.setContenidoFoto(Base64.getEncoder().encodeToString(response.getBody()));
        noticia.setContenTypeFoto(response.getHeaders().getContentType().toString());
        return noticia;
    }
}
