package se.vgregion.vatskenutrition.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import se.vgregion.vatskenutrition.model.v2.Article;
import se.vgregion.vatskenutrition.model.v2.ItemResponse;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;

public class ArticleServiceTest {

    @Before
    public void setup() {
    }

    @Test
    public void parseJson() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("articles.json");

        ObjectMapper mapper = new ObjectMapper();

        ItemResponse<Article> object = mapper.readValue(inputStream, new TypeReference<>() {
        });

        assertFalse(object.getItems().isEmpty());

    }
}
