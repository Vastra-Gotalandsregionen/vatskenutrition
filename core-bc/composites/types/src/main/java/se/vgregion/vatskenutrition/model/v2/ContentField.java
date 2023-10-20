package se.vgregion.vatskenutrition.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentField {

    private ContentFieldValue contentFieldValue;
    private String dataType;
    private String label;
    private String name;
    private List<ContentField> nestedContentFields;
}
