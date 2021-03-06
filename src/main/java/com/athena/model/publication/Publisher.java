package com.athena.model.publication;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.List;

/**
 * Created by tommy on 2017/3/28.
 */
@Entity
@Table(name = "publisher")
public class Publisher {
    private String id;
    private String name;
    private String location;

    @ApiModelProperty(hidden = true)
    private List<Book> books;
    @ApiModelProperty(hidden = true)
    private List<Journal> journals;
    @ApiModelProperty(hidden = true)
    private List<Audio> audios;

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @OneToMany(mappedBy = "publisher")
    @JsonBackReference(value = "book")
    @JsonIgnore
    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @OneToMany(mappedBy = "publisher")
    @JsonBackReference(value = "journal")
    @JsonIgnore
    public List<Journal> getJournals() {
        return journals;
    }

    public void setJournals(List<Journal> journals) {
        this.journals = journals;
    }

    @Basic
    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @OneToMany(mappedBy = "publisher")
    @JsonBackReference(value = "audio")
    @JsonIgnore
    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;

        if (!id.equals(publisher.id)) return false;
        if (!name.equals(publisher.name)) return false;
        return location.equals(publisher.location);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
}
