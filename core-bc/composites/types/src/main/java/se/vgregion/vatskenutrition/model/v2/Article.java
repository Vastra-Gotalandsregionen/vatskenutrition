package se.vgregion.vatskenutrition.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(of = {"title", "structuredContentFolderId", "uuid"})
public class Article {

    private List<ContentField> contentFields;
    private String title;
    private String uuid;
    private String contentStructureId;
    private Integer siteId;
    private Integer structuredContentFolderId;

    private List<String> path;
}
