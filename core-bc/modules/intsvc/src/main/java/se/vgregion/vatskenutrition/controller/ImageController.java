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
import java.net.URL;
import java.net.URLConnection;

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

        URL url2 = new URL(baseUrl + url);

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
