package se.vgregion.vatskenutrition.model.v2;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResponse<T> {

    @JsonProperty("items")
    private List<T> items;

    private Integer page;
    private Integer pageSize;
    private Integer totalCount;
}
