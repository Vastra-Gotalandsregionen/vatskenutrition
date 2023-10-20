package se.vgregion.vatskenutrition.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Value("${baseUrl}")
    private String baseUrl;

    // /documents/{groupId}/{uuid}
    private static final String uriFormat = "/documents/%s/%s";

    /**
     * @deprecated
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(@RequestParam("url") String url) throws IOException {

        String path = url
                .replaceAll(" ", "%20")
                .replaceAll("å", URLEncoder.encode("å", StandardCharsets.UTF_8))
                .replaceAll("ä", URLEncoder.encode("ä", StandardCharsets.UTF_8))
                .replaceAll("ö", URLEncoder.encode("ö", StandardCharsets.UTF_8))
                .replaceAll("Å", URLEncoder.encode("Å", StandardCharsets.UTF_8))
                .replaceAll("Ä", URLEncoder.encode("Ä", StandardCharsets.UTF_8))
                .replaceAll("Ö", URLEncoder.encode("Ö", StandardCharsets.UTF_8))
                ;

        URL url2 = new URL(baseUrl + path);

        try {
            URI uri = new URI(url2.getProtocol(), url2.getHost(), url2.getPath(), url2.getQuery(),null);
            HttpResponse<String> send = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder().GET().uri(
                            uri
                    ).build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            send.headers();
        } catch (InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return getImage(url2);
    }

    @RequestMapping(value = "/{groupId}/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("groupId") String groupId,
                                                        @PathVariable("uuid") String uuid) throws IOException {

        String url = formatUrl(groupId, uuid);

        URL url2 = new URL(baseUrl + url);

        return getImage(url2);
    }

    private ResponseEntity<InputStreamResource> getImage(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();

        String contentType = urlConnection.getHeaderField("Content-Type");

        if (!contentType.contains("/")) {
            contentType = "image/" + contentType;
        }

        InputStream inputStream = urlConnection.getInputStream();

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(inputStreamResource);
    }

    static String formatUrl(@RequestParam("groupId") String groupId, @RequestParam("uuid") String uuid) {
        return String.format(uriFormat, groupId, uuid);
    }

}
