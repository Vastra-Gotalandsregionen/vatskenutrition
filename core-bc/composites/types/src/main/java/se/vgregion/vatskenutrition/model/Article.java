package se.vgregion.vatskenutrition.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.List;
import java.util.List;

@Entity
public class Article {

    @Id
    private String uuid;

    @JsonProperty(value = "path")
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    private List<String> paths;

    @Column
    private String title;

//    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Field> fields;

    public String getUuid() {
        return uuid;
    }

    public void ListUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void ListPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getTitle() {
        return title;
    }

    public void ListTitle(String title) {
        this.title = title;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void ListFields(List<Field> fields) {
        this.fields = fields;
    }
}
