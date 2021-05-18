package com.ar.mangerspital.domain;

import com.ar.mangerspital.domain.enumeration.TagInventar;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Inventar.
 */
@Entity
@Table(name = "inventar")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "inventar")
public class Inventar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "inventar_id", nullable = false)
    private Long inventarId;

    @NotNull
    @Column(name = "nume", nullable = false)
    private String nume;

    @NotNull
    @Column(name = "stoc", nullable = false)
    private Integer stoc;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false)
    private TagInventar tag;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Inventar id(Long id) {
        this.id = id;
        return this;
    }

    public Long getInventarId() {
        return this.inventarId;
    }

    public Inventar inventarId(Long inventarId) {
        this.inventarId = inventarId;
        return this;
    }

    public void setInventarId(Long inventarId) {
        this.inventarId = inventarId;
    }

    public String getNume() {
        return this.nume;
    }

    public Inventar nume(String nume) {
        this.nume = nume;
        return this;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Integer getStoc() {
        return this.stoc;
    }

    public Inventar stoc(Integer stoc) {
        this.stoc = stoc;
        return this;
    }

    public void setStoc(Integer stoc) {
        this.stoc = stoc;
    }

    public TagInventar getTag() {
        return this.tag;
    }

    public Inventar tag(TagInventar tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(TagInventar tag) {
        this.tag = tag;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inventar)) {
            return false;
        }
        return id != null && id.equals(((Inventar) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inventar{" +
            "id=" + getId() +
            ", inventarId=" + getInventarId() +
            ", nume='" + getNume() + "'" +
            ", stoc=" + getStoc() +
            ", tag='" + getTag() + "'" +
            "}";
    }
}
