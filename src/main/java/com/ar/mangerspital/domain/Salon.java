package com.ar.mangerspital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Salon.
 */
@Entity
@Table(name = "salon")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salon")
public class Salon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "salon_id")
    private Long salonId;

    @Column(name = "sectie_id")
    private Long sectieId;

    @OneToMany(mappedBy = "salon")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "personal", "sectie", "salon" }, allowSetters = true)
    private Set<Pacient> salonIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Salon id(Long id) {
        this.id = id;
        return this;
    }

    public Long getSalonId() {
        return this.salonId;
    }

    public Salon salonId(Long salonId) {
        this.salonId = salonId;
        return this;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public Long getSectieId() {
        return this.sectieId;
    }

    public Salon sectieId(Long sectieId) {
        this.sectieId = sectieId;
        return this;
    }

    public void setSectieId(Long sectieId) {
        this.sectieId = sectieId;
    }

    public Set<Pacient> getSalonIds() {
        return this.salonIds;
    }

    public Salon salonIds(Set<Pacient> pacients) {
        this.setSalonIds(pacients);
        return this;
    }

    public Salon addSalonId(Pacient pacient) {
        this.salonIds.add(pacient);
        pacient.setSalon(this);
        return this;
    }

    public Salon removeSalonId(Pacient pacient) {
        this.salonIds.remove(pacient);
        pacient.setSalon(null);
        return this;
    }

    public void setSalonIds(Set<Pacient> pacients) {
        if (this.salonIds != null) {
            this.salonIds.forEach(i -> i.setSalon(null));
        }
        if (pacients != null) {
            pacients.forEach(i -> i.setSalon(this));
        }
        this.salonIds = pacients;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Salon)) {
            return false;
        }
        return id != null && id.equals(((Salon) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Salon{" +
            "id=" + getId() +
            ", salonId=" + getSalonId() +
            ", sectieId=" + getSectieId() +
            "}";
    }
}
