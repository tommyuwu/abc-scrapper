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

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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
                    noticia.setContenidoFoto(traerFoto(noticia.getEnlaceFoto()));
                    noticia.setContenTypeFoto("image/jpeg");
                }
                noticias.add(noticia);
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

    public String traerFoto(String url){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return Base64.getEncoder().encodeToString(response.getBody());
    }
}
