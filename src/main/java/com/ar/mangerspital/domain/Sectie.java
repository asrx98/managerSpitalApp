package com.ar.mangerspital.domain;

import com.ar.mangerspital.domain.enumeration.TagSectie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Sectie.
 */
@Entity
@Table(name = "sectie")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sectie")
public class Sectie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "sectie_id", nullable = false)
    private Long sectieId;

    @NotNull
    @Column(name = "nume", nullable = false)
    private String nume;

    @NotNull
    @Column(name = "sef_id", nullable = false)
    private String sefId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false)
    private TagSectie tag;

    @NotNull
    @Column(name = "nr_paturi", nullable = false)
    private Integer nrPaturi;

    @OneToMany(mappedBy = "sectie")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "personal", "sectie", "salon" }, allowSetters = true)
    private Set<Pacient> sectieIds = new HashSet<>();

    @OneToMany(mappedBy = "sectie")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "personalId", "personalIds", "sectie" }, allowSetters = true)
    private Set<Personal> sectieIds = new HashSet<>();

    @JsonIgnoreProperties(value = { "personalId", "personalIds", "sectie" }, allowSetters = true)
    @OneToOne(mappedBy = "personalId")
    private Personal sefId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sectie id(Long id) {
        this.id = id;
        return this;
    }

    public Long getSectieId() {
        return this.sectieId;
    }

    public Sectie sectieId(Long sectieId) {
        this.sectieId = sectieId;
        return this;
    }

    public void setSectieId(Long sectieId) {
        this.sectieId = sectieId;
    }

    public String getNume() {
        return this.nume;
    }

    public Sectie nume(String nume) {
        this.nume = nume;
        return this;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getSefId() {
        return this.sefId;
    }

    public Sectie sefId(String sefId) {
        this.sefId = sefId;
        return this;
    }

    public void setSefId(String sefId) {
        this.sefId = sefId;
    }

    public TagSectie getTag() {
        return this.tag;
    }

    public Sectie tag(TagSectie tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(TagSectie tag) {
        this.tag = tag;
    }

    public Integer getNrPaturi() {
        return this.nrPaturi;
    }

    public Sectie nrPaturi(Integer nrPaturi) {
        this.nrPaturi = nrPaturi;
        return this;
    }

    public void setNrPaturi(Integer nrPaturi) {
        this.nrPaturi = nrPaturi;
    }

    public Set<Pacient> getSectieIds() {
        return this.sectieIds;
    }

    public Sectie sectieIds(Set<Pacient> pacients) {
        this.setSectieIds(pacients);
        return this;
    }

    public Sectie addSectieId(Pacient pacient) {
        this.sectieIds.add(pacient);
        pacient.setSectie(this);
        return this;
    }

    public Sectie removeSectieId(Pacient pacient) {
        this.sectieIds.remove(pacient);
        pacient.setSectie(null);
        return this;
    }

    public void setSectieIds(Set<Pacient> pacients) {
        if (this.sectieIds != null) {
            this.sectieIds.forEach(i -> i.setSectie(null));
        }
        if (pacients != null) {
            pacients.forEach(i -> i.setSectie(this));
        }
        this.sectieIds = pacients;
    }

    public Set<Personal> getSectieIds() {
        return this.sectieIds;
    }

    public Sectie sectieIds(Set<Personal> personals) {
        this.setSectieIds(personals);
        return this;
    }

    public Sectie addSectieId(Personal personal) {
        this.sectieIds.add(personal);
        personal.setSectie(this);
        return this;
    }

    public Sectie removeSectieId(Personal personal) {
        this.sectieIds.remove(personal);
        personal.setSectie(null);
        return this;
    }

    public void setSectieIds(Set<Personal> personals) {
        if (this.sectieIds != null) {
            this.sectieIds.forEach(i -> i.setSectie(null));
        }
        if (personals != null) {
            personals.forEach(i -> i.setSectie(this));
        }
        this.sectieIds = personals;
    }

    public Personal getSefId() {
        return this.sefId;
    }

    public Sectie sefId(Personal personal) {
        this.setSefId(personal);
        return this;
    }

    public void setSefId(Personal personal) {
        if (this.sefId != null) {
            this.sefId.setPersonalId(null);
        }
        if (sefId != null) {
            sefId.setPersonalId(this);
        }
        this.sefId = personal;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sectie)) {
            return false;
        }
        return id != null && id.equals(((Sectie) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sectie{" +
            "id=" + getId() +
            ", sectieId=" + getSectieId() +
            ", nume='" + getNume() + "'" +
            ", sefId='" + getSefId() + "'" +
            ", tag='" + getTag() + "'" +
            ", nrPaturi=" + getNrPaturi() +
            "}";
    }
}
