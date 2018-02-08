package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Value("${baseUrl}")
    private String baseUrl;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "image/png")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getArticlesByYear(@RequestParam("url") String url) throws IOException {

        URL url2 = new URL(baseUrl + url);

        InputStream inputStream = url2.openStream();

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        return ResponseEntity.ok(inputStreamResource);
    }

}
