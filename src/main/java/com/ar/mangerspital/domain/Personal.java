package com.ar.mangerspital.domain;

import com.ar.mangerspital.domain.enumeration.TagPersonal;
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
 * A Personal.
 */
@Entity
@Table(name = "personal")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "personal")
public class Personal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "pacient_id", nullable = false)
    private Long pacientId;

    @NotNull
    @Column(name = "nume", nullable = false)
    private String nume;

    @NotNull
    @Column(name = "prenume", nullable = false)
    private String prenume;

    @NotNull
    @Column(name = "sectie_id", nullable = false)
    private String sectieId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false)
    private TagPersonal tag;

    @JsonIgnoreProperties(value = { "sectieIds", "sectieIds", "sefId" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Sectie personalId;

    @OneToMany(mappedBy = "personal")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "personal", "sectie", "salon" }, allowSetters = true)
    private Set<Pacient> personalIds = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "sectieIds", "sectieIds", "sefId" }, allowSetters = true)
    private Sectie sectie;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Personal id(Long id) {
        this.id = id;
        return this;
    }

    public Long getPacientId() {
        return this.pacientId;
    }

    public Personal pacientId(Long pacientId) {
        this.pacientId = pacientId;
        return this;
    }

    public void setPacientId(Long pacientId) {
        this.pacientId = pacientId;
    }

    public String getNume() {
        return this.nume;
    }

    public Personal nume(String nume) {
        this.nume = nume;
        return this;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return this.prenume;
    }

    public Personal prenume(String prenume) {
        this.prenume = prenume;
        return this;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getSectieId() {
        return this.sectieId;
    }

    public Personal sectieId(String sectieId) {
        this.sectieId = sectieId;
        return this;
    }

    public void setSectieId(String sectieId) {
        this.sectieId = sectieId;
    }

    public TagPersonal getTag() {
        return this.tag;
    }

    public Personal tag(TagPersonal tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(TagPersonal tag) {
        this.tag = tag;
    }

    public Sectie getPersonalId() {
        return this.personalId;
    }

    public Personal personalId(Sectie sectie) {
        this.setPersonalId(sectie);
        return this;
    }

    public void setPersonalId(Sectie sectie) {
        this.personalId = sectie;
    }

    public Set<Pacient> getPersonalIds() {
        return this.personalIds;
    }

    public Personal personalIds(Set<Pacient> pacients) {
        this.setPersonalIds(pacients);
        return this;
    }

    public Personal addPersonalId(Pacient pacient) {
        this.personalIds.add(pacient);
        pacient.setPersonal(this);
        return this;
    }

    public Personal removePersonalId(Pacient pacient) {
        this.personalIds.remove(pacient);
        pacient.setPersonal(null);
        return this;
    }

    public void setPersonalIds(Set<Pacient> pacients) {
        if (this.personalIds != null) {
            this.personalIds.forEach(i -> i.setPersonal(null));
        }
        if (pacients != null) {
            pacients.forEach(i -> i.setPersonal(this));
        }
        this.personalIds = pacients;
    }

    public Sectie getSectie() {
        return this.sectie;
    }

    public Personal sectie(Sectie sectie) {
        this.setSectie(sectie);
        return this;
    }

    public void setSectie(Sectie sectie) {
        this.sectie = sectie;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Personal)) {
            return false;
        }
        return id != null && id.equals(((Personal) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Personal{" +
            "id=" + getId() +
            ", pacientId=" + getPacientId() +
            ", nume='" + getNume() + "'" +
            ", prenume='" + getPrenume() + "'" +
            ", sectieId='" + getSectieId() + "'" +
            ", tag='" + getTag() + "'" +
            "}";
    }
}
