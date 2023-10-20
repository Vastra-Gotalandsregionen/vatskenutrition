package se.vgregion.vatskenutrition.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {

    private List<ContentField> contentFields;
    private String title;
    private String uuid;
    private String contentStructureId;
    private Integer siteId;
    private Integer structuredContentFolderId;

    private List<String> path;
}
