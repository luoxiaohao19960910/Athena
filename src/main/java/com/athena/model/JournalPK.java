package com.athena.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Tommy on 2017/8/28.
 */
public class JournalPK implements Serializable {
    private String issn;
    private Integer year;
    private Integer index;

    public JournalPK(){
    }

    public JournalPK(String issn, Integer year, Integer index) {
        this.issn = issn;
        this.year = year;
        this.index = index;
    }

    @Column(name = "issn", nullable = false, length = 8)
    @Id
    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    @Column(name = "year", nullable = false)
    @Id
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "index", nullable = false)
    @Id
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JournalPK journalPK = (JournalPK) o;

        if (issn != null ? !issn.equals(journalPK.issn) : journalPK.issn != null) return false;
        if (year != null ? !year.equals(journalPK.year) : journalPK.year != null) return false;
        if (index != null ? !index.equals(journalPK.index) : journalPK.index != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = issn != null ? issn.hashCode() : 0;
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (index != null ? index.hashCode() : 0);
        return result;
    }
}
