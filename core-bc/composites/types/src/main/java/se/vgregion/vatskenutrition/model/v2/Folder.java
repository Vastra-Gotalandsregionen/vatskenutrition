package se.vgregion.vatskenutrition.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Folder {

    private Integer id;
    private Integer parentStructuredContentFolderId;
    private String name;
}
