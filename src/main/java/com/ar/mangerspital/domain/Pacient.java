package com.ar.mangerspital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Pacient.
 */
@Entity
@Table(name = "pacient")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "pacient")
public class Pacient implements Serializable {

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
    @Column(name = "salon_id", nullable = false)
    private String salonId;

    @ManyToOne
    @JsonIgnoreProperties(value = { "personalId", "personalIds", "sectie" }, allowSetters = true)
    private Personal personal;

    @ManyToOne
    @JsonIgnoreProperties(value = { "sectieIds", "sectieIds", "sefId" }, allowSetters = true)
    private Sectie sectie;

    @ManyToOne
    @JsonIgnoreProperties(value = { "salonIds" }, allowSetters = true)
    private Salon salon;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pacient id(Long id) {
        this.id = id;
        return this;
    }

    public Long getPacientId() {
        return this.pacientId;
    }

    public Pacient pacientId(Long pacientId) {
        this.pacientId = pacientId;
        return this;
    }

    public void setPacientId(Long pacientId) {
        this.pacientId = pacientId;
    }

    public String getNume() {
        return this.nume;
    }

    public Pacient nume(String nume) {
        this.nume = nume;
        return this;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return this.prenume;
    }

    public Pacient prenume(String prenume) {
        this.prenume = prenume;
        return this;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getSectieId() {
        return this.sectieId;
    }

    public Pacient sectieId(String sectieId) {
        this.sectieId = sectieId;
        return this;
    }

    public void setSectieId(String sectieId) {
        this.sectieId = sectieId;
    }

    public String getSalonId() {
        return this.salonId;
    }

    public Pacient salonId(String salonId) {
        this.salonId = salonId;
        return this;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public Personal getPersonal() {
        return this.personal;
    }

    public Pacient personal(Personal personal) {
        this.setPersonal(personal);
        return this;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public Sectie getSectie() {
        return this.sectie;
    }

    public Pacient sectie(Sectie sectie) {
        this.setSectie(sectie);
        return this;
    }

    public void setSectie(Sectie sectie) {
        this.sectie = sectie;
    }

    public Salon getSalon() {
        return this.salon;
    }

    public Pacient salon(Salon salon) {
        this.setSalon(salon);
        return this;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pacient)) {
            return false;
        }
        return id != null && id.equals(((Pacient) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pacient{" +
            "id=" + getId() +
            ", pacientId=" + getPacientId() +
            ", nume='" + getNume() + "'" +
            ", prenume='" + getPrenume() + "'" +
            ", sectieId='" + getSectieId() + "'" +
            ", salonId='" + getSalonId() + "'" +
            "}";
    }
}
