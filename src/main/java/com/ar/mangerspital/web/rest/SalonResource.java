package com.ar.mangerspital.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ar.mangerspital.domain.Salon;
import com.ar.mangerspital.repository.SalonRepository;
import com.ar.mangerspital.repository.search.SalonSearchRepository;
import com.ar.mangerspital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ar.mangerspital.domain.Salon}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SalonResource {

    private final Logger log = LoggerFactory.getLogger(SalonResource.class);

    private static final String ENTITY_NAME = "salon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalonRepository salonRepository;

    private final SalonSearchRepository salonSearchRepository;

    public SalonResource(SalonRepository salonRepository, SalonSearchRepository salonSearchRepository) {
        this.salonRepository = salonRepository;
        this.salonSearchRepository = salonSearchRepository;
    }

    /**
     * {@code POST  /salons} : Create a new salon.
     *
     * @param salon the salon to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salon, or with status {@code 400 (Bad Request)} if the salon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/salons")
    public ResponseEntity<Salon> createSalon(@RequestBody Salon salon) throws URISyntaxException {
        log.debug("REST request to save Salon : {}", salon);
        if (salon.getId() != null) {
            throw new BadRequestAlertException("A new salon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Salon result = salonRepository.save(salon);
        salonSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/salons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /salons/:id} : Updates an existing salon.
     *
     * @param id the id of the salon to save.
     * @param salon the salon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salon,
     * or with status {@code 400 (Bad Request)} if the salon is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/salons/{id}")
    public ResponseEntity<Salon> updateSalon(@PathVariable(value = "id", required = false) final Long id, @RequestBody Salon salon)
        throws URISyntaxException {
        log.debug("REST request to update Salon : {}, {}", id, salon);
        if (salon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Salon result = salonRepository.save(salon);
        salonSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salon.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /salons/:id} : Partial updates given fields of an existing salon, field will ignore if it is null
     *
     * @param id the id of the salon to save.
     * @param salon the salon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salon,
     * or with status {@code 400 (Bad Request)} if the salon is not valid,
     * or with status {@code 404 (Not Found)} if the salon is not found,
     * or with status {@code 500 (Internal Server Error)} if the salon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/salons/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Salon> partialUpdateSalon(@PathVariable(value = "id", required = false) final Long id, @RequestBody Salon salon)
        throws URISyntaxException {
        log.debug("REST request to partial update Salon partially : {}, {}", id, salon);
        if (salon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Salon> result = salonRepository
            .findById(salon.getId())
            .map(
                existingSalon -> {
                    if (salon.getSalonId() != null) {
                        existingSalon.setSalonId(salon.getSalonId());
                    }
                    if (salon.getSectieId() != null) {
                        existingSalon.setSectieId(salon.getSectieId());
                    }

                    return existingSalon;
                }
            )
            .map(salonRepository::save)
            .map(
                savedSalon -> {
                    salonSearchRepository.save(savedSalon);

                    return savedSalon;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salon.getId().toString())
        );
    }

    /**
     * {@code GET  /salons} : get all the salons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salons in body.
     */
    @GetMapping("/salons")
    public List<Salon> getAllSalons() {
        log.debug("REST request to get all Salons");
        return salonRepository.findAll();
    }

    /**
     * {@code GET  /salons/:id} : get the "id" salon.
     *
     * @param id the id of the salon to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salon, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/salons/{id}")
    public ResponseEntity<Salon> getSalon(@PathVariable Long id) {
        log.debug("REST request to get Salon : {}", id);
        Optional<Salon> salon = salonRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(salon);
    }

    /**
     * {@code DELETE  /salons/:id} : delete the "id" salon.
     *
     * @param id the id of the salon to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/salons/{id}")
    public ResponseEntity<Void> deleteSalon(@PathVariable Long id) {
        log.debug("REST request to delete Salon : {}", id);
        salonRepository.deleteById(id);
        salonSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/salons?query=:query} : search for the salon corresponding
     * to the query.
     *
     * @param query the query of the salon search.
     * @return the result of the search.
     */
    @GetMapping("/_search/salons")
    public List<Salon> searchSalons(@RequestParam String query) {
        log.debug("REST request to search Salons for query {}", query);
        return StreamSupport
            .stream(salonSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
