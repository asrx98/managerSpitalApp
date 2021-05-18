package com.ar.mangerspital.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ar.mangerspital.domain.Pacient;
import com.ar.mangerspital.repository.PacientRepository;
import com.ar.mangerspital.repository.search.PacientSearchRepository;
import com.ar.mangerspital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ar.mangerspital.domain.Pacient}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PacientResource {

    private final Logger log = LoggerFactory.getLogger(PacientResource.class);

    private static final String ENTITY_NAME = "pacient";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PacientRepository pacientRepository;

    private final PacientSearchRepository pacientSearchRepository;

    public PacientResource(PacientRepository pacientRepository, PacientSearchRepository pacientSearchRepository) {
        this.pacientRepository = pacientRepository;
        this.pacientSearchRepository = pacientSearchRepository;
    }

    /**
     * {@code POST  /pacients} : Create a new pacient.
     *
     * @param pacient the pacient to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pacient, or with status {@code 400 (Bad Request)} if the pacient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pacients")
    public ResponseEntity<Pacient> createPacient(@Valid @RequestBody Pacient pacient) throws URISyntaxException {
        log.debug("REST request to save Pacient : {}", pacient);
        if (pacient.getId() != null) {
            throw new BadRequestAlertException("A new pacient cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Pacient result = pacientRepository.save(pacient);
        pacientSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/pacients/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pacients/:id} : Updates an existing pacient.
     *
     * @param id the id of the pacient to save.
     * @param pacient the pacient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pacient,
     * or with status {@code 400 (Bad Request)} if the pacient is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pacient couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pacients/{id}")
    public ResponseEntity<Pacient> updatePacient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Pacient pacient
    ) throws URISyntaxException {
        log.debug("REST request to update Pacient : {}, {}", id, pacient);
        if (pacient.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pacient.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pacientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Pacient result = pacientRepository.save(pacient);
        pacientSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pacient.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pacients/:id} : Partial updates given fields of an existing pacient, field will ignore if it is null
     *
     * @param id the id of the pacient to save.
     * @param pacient the pacient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pacient,
     * or with status {@code 400 (Bad Request)} if the pacient is not valid,
     * or with status {@code 404 (Not Found)} if the pacient is not found,
     * or with status {@code 500 (Internal Server Error)} if the pacient couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pacients/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Pacient> partialUpdatePacient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Pacient pacient
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pacient partially : {}, {}", id, pacient);
        if (pacient.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pacient.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pacientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Pacient> result = pacientRepository
            .findById(pacient.getId())
            .map(
                existingPacient -> {
                    if (pacient.getPacientId() != null) {
                        existingPacient.setPacientId(pacient.getPacientId());
                    }
                    if (pacient.getNume() != null) {
                        existingPacient.setNume(pacient.getNume());
                    }
                    if (pacient.getPrenume() != null) {
                        existingPacient.setPrenume(pacient.getPrenume());
                    }
                    if (pacient.getSectieId() != null) {
                        existingPacient.setSectieId(pacient.getSectieId());
                    }
                    if (pacient.getSalonId() != null) {
                        existingPacient.setSalonId(pacient.getSalonId());
                    }

                    return existingPacient;
                }
            )
            .map(pacientRepository::save)
            .map(
                savedPacient -> {
                    pacientSearchRepository.save(savedPacient);

                    return savedPacient;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pacient.getId().toString())
        );
    }

    /**
     * {@code GET  /pacients} : get all the pacients.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pacients in body.
     */
    @GetMapping("/pacients")
    public ResponseEntity<List<Pacient>> getAllPacients(Pageable pageable) {
        log.debug("REST request to get a page of Pacients");
        Page<Pacient> page = pacientRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pacients/:id} : get the "id" pacient.
     *
     * @param id the id of the pacient to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pacient, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pacients/{id}")
    public ResponseEntity<Pacient> getPacient(@PathVariable Long id) {
        log.debug("REST request to get Pacient : {}", id);
        Optional<Pacient> pacient = pacientRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pacient);
    }

    /**
     * {@code DELETE  /pacients/:id} : delete the "id" pacient.
     *
     * @param id the id of the pacient to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pacients/{id}")
    public ResponseEntity<Void> deletePacient(@PathVariable Long id) {
        log.debug("REST request to delete Pacient : {}", id);
        pacientRepository.deleteById(id);
        pacientSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/pacients?query=:query} : search for the pacient corresponding
     * to the query.
     *
     * @param query the query of the pacient search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/pacients")
    public ResponseEntity<List<Pacient>> searchPacients(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Pacients for query {}", query);
        Page<Pacient> page = pacientSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
