package se.vgregion.vatskenutrition.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

    private Long id;
    private String contentUrl;

}
