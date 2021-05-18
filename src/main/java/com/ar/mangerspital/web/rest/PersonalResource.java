package com.ar.mangerspital.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ar.mangerspital.domain.Personal;
import com.ar.mangerspital.repository.PersonalRepository;
import com.ar.mangerspital.repository.search.PersonalSearchRepository;
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
 * REST controller for managing {@link com.ar.mangerspital.domain.Personal}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PersonalResource {

    private final Logger log = LoggerFactory.getLogger(PersonalResource.class);

    private static final String ENTITY_NAME = "personal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonalRepository personalRepository;

    private final PersonalSearchRepository personalSearchRepository;

    public PersonalResource(PersonalRepository personalRepository, PersonalSearchRepository personalSearchRepository) {
        this.personalRepository = personalRepository;
        this.personalSearchRepository = personalSearchRepository;
    }

    /**
     * {@code POST  /personals} : Create a new personal.
     *
     * @param personal the personal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personal, or with status {@code 400 (Bad Request)} if the personal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/personals")
    public ResponseEntity<Personal> createPersonal(@Valid @RequestBody Personal personal) throws URISyntaxException {
        log.debug("REST request to save Personal : {}", personal);
        if (personal.getId() != null) {
            throw new BadRequestAlertException("A new personal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Personal result = personalRepository.save(personal);
        personalSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/personals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /personals/:id} : Updates an existing personal.
     *
     * @param id the id of the personal to save.
     * @param personal the personal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personal,
     * or with status {@code 400 (Bad Request)} if the personal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/personals/{id}")
    public ResponseEntity<Personal> updatePersonal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Personal personal
    ) throws URISyntaxException {
        log.debug("REST request to update Personal : {}, {}", id, personal);
        if (personal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Personal result = personalRepository.save(personal);
        personalSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personal.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /personals/:id} : Partial updates given fields of an existing personal, field will ignore if it is null
     *
     * @param id the id of the personal to save.
     * @param personal the personal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personal,
     * or with status {@code 400 (Bad Request)} if the personal is not valid,
     * or with status {@code 404 (Not Found)} if the personal is not found,
     * or with status {@code 500 (Internal Server Error)} if the personal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/personals/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Personal> partialUpdatePersonal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Personal personal
    ) throws URISyntaxException {
        log.debug("REST request to partial update Personal partially : {}, {}", id, personal);
        if (personal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Personal> result = personalRepository
            .findById(personal.getId())
            .map(
                existingPersonal -> {
                    if (personal.getPacientId() != null) {
                        existingPersonal.setPacientId(personal.getPacientId());
                    }
                    if (personal.getNume() != null) {
                        existingPersonal.setNume(personal.getNume());
                    }
                    if (personal.getPrenume() != null) {
                        existingPersonal.setPrenume(personal.getPrenume());
                    }
                    if (personal.getSectieId() != null) {
                        existingPersonal.setSectieId(personal.getSectieId());
                    }
                    if (personal.getTag() != null) {
                        existingPersonal.setTag(personal.getTag());
                    }

                    return existingPersonal;
                }
            )
            .map(personalRepository::save)
            .map(
                savedPersonal -> {
                    personalSearchRepository.save(savedPersonal);

                    return savedPersonal;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personal.getId().toString())
        );
    }

    /**
     * {@code GET  /personals} : get all the personals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personals in body.
     */
    @GetMapping("/personals")
    public ResponseEntity<List<Personal>> getAllPersonals(Pageable pageable) {
        log.debug("REST request to get a page of Personals");
        Page<Personal> page = personalRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /personals/:id} : get the "id" personal.
     *
     * @param id the id of the personal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/personals/{id}")
    public ResponseEntity<Personal> getPersonal(@PathVariable Long id) {
        log.debug("REST request to get Personal : {}", id);
        Optional<Personal> personal = personalRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(personal);
    }

    /**
     * {@code DELETE  /personals/:id} : delete the "id" personal.
     *
     * @param id the id of the personal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/personals/{id}")
    public ResponseEntity<Void> deletePersonal(@PathVariable Long id) {
        log.debug("REST request to delete Personal : {}", id);
        personalRepository.deleteById(id);
        personalSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/personals?query=:query} : search for the personal corresponding
     * to the query.
     *
     * @param query the query of the personal search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/personals")
    public ResponseEntity<List<Personal>> searchPersonals(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Personals for query {}", query);
        Page<Personal> page = personalSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
