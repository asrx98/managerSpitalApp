package com.ar.mangerspital.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ar.mangerspital.domain.Inventar;
import com.ar.mangerspital.repository.InventarRepository;
import com.ar.mangerspital.repository.search.InventarSearchRepository;
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
 * REST controller for managing {@link com.ar.mangerspital.domain.Inventar}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class InventarResource {

    private final Logger log = LoggerFactory.getLogger(InventarResource.class);

    private static final String ENTITY_NAME = "inventar";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InventarRepository inventarRepository;

    private final InventarSearchRepository inventarSearchRepository;

    public InventarResource(InventarRepository inventarRepository, InventarSearchRepository inventarSearchRepository) {
        this.inventarRepository = inventarRepository;
        this.inventarSearchRepository = inventarSearchRepository;
    }

    /**
     * {@code POST  /inventars} : Create a new inventar.
     *
     * @param inventar the inventar to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inventar, or with status {@code 400 (Bad Request)} if the inventar has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/inventars")
    public ResponseEntity<Inventar> createInventar(@Valid @RequestBody Inventar inventar) throws URISyntaxException {
        log.debug("REST request to save Inventar : {}", inventar);
        if (inventar.getId() != null) {
            throw new BadRequestAlertException("A new inventar cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Inventar result = inventarRepository.save(inventar);
        inventarSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/inventars/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /inventars/:id} : Updates an existing inventar.
     *
     * @param id the id of the inventar to save.
     * @param inventar the inventar to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventar,
     * or with status {@code 400 (Bad Request)} if the inventar is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inventar couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/inventars/{id}")
    public ResponseEntity<Inventar> updateInventar(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Inventar inventar
    ) throws URISyntaxException {
        log.debug("REST request to update Inventar : {}, {}", id, inventar);
        if (inventar.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventar.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventarRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Inventar result = inventarRepository.save(inventar);
        inventarSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventar.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /inventars/:id} : Partial updates given fields of an existing inventar, field will ignore if it is null
     *
     * @param id the id of the inventar to save.
     * @param inventar the inventar to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventar,
     * or with status {@code 400 (Bad Request)} if the inventar is not valid,
     * or with status {@code 404 (Not Found)} if the inventar is not found,
     * or with status {@code 500 (Internal Server Error)} if the inventar couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/inventars/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Inventar> partialUpdateInventar(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Inventar inventar
    ) throws URISyntaxException {
        log.debug("REST request to partial update Inventar partially : {}, {}", id, inventar);
        if (inventar.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventar.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventarRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Inventar> result = inventarRepository
            .findById(inventar.getId())
            .map(
                existingInventar -> {
                    if (inventar.getInventarId() != null) {
                        existingInventar.setInventarId(inventar.getInventarId());
                    }
                    if (inventar.getNume() != null) {
                        existingInventar.setNume(inventar.getNume());
                    }
                    if (inventar.getStoc() != null) {
                        existingInventar.setStoc(inventar.getStoc());
                    }
                    if (inventar.getTag() != null) {
                        existingInventar.setTag(inventar.getTag());
                    }

                    return existingInventar;
                }
            )
            .map(inventarRepository::save)
            .map(
                savedInventar -> {
                    inventarSearchRepository.save(savedInventar);

                    return savedInventar;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventar.getId().toString())
        );
    }

    /**
     * {@code GET  /inventars} : get all the inventars.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inventars in body.
     */
    @GetMapping("/inventars")
    public ResponseEntity<List<Inventar>> getAllInventars(Pageable pageable) {
        log.debug("REST request to get a page of Inventars");
        Page<Inventar> page = inventarRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inventars/:id} : get the "id" inventar.
     *
     * @param id the id of the inventar to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventar, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/inventars/{id}")
    public ResponseEntity<Inventar> getInventar(@PathVariable Long id) {
        log.debug("REST request to get Inventar : {}", id);
        Optional<Inventar> inventar = inventarRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(inventar);
    }

    /**
     * {@code DELETE  /inventars/:id} : delete the "id" inventar.
     *
     * @param id the id of the inventar to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/inventars/{id}")
    public ResponseEntity<Void> deleteInventar(@PathVariable Long id) {
        log.debug("REST request to delete Inventar : {}", id);
        inventarRepository.deleteById(id);
        inventarSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/inventars?query=:query} : search for the inventar corresponding
     * to the query.
     *
     * @param query the query of the inventar search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/inventars")
    public ResponseEntity<List<Inventar>> searchInventars(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Inventars for query {}", query);
        Page<Inventar> page = inventarSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
