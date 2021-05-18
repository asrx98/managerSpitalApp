package com.ar.mangerspital.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ar.mangerspital.domain.Sectie;
import com.ar.mangerspital.repository.SectieRepository;
import com.ar.mangerspital.repository.search.SectieSearchRepository;
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
 * REST controller for managing {@link com.ar.mangerspital.domain.Sectie}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SectieResource {

    private final Logger log = LoggerFactory.getLogger(SectieResource.class);

    private static final String ENTITY_NAME = "sectie";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SectieRepository sectieRepository;

    private final SectieSearchRepository sectieSearchRepository;

    public SectieResource(SectieRepository sectieRepository, SectieSearchRepository sectieSearchRepository) {
        this.sectieRepository = sectieRepository;
        this.sectieSearchRepository = sectieSearchRepository;
    }

    /**
     * {@code POST  /secties} : Create a new sectie.
     *
     * @param sectie the sectie to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sectie, or with status {@code 400 (Bad Request)} if the sectie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/secties")
    public ResponseEntity<Sectie> createSectie(@Valid @RequestBody Sectie sectie) throws URISyntaxException {
        log.debug("REST request to save Sectie : {}", sectie);
        if (sectie.getId() != null) {
            throw new BadRequestAlertException("A new sectie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sectie result = sectieRepository.save(sectie);
        sectieSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/secties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /secties/:id} : Updates an existing sectie.
     *
     * @param id the id of the sectie to save.
     * @param sectie the sectie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sectie,
     * or with status {@code 400 (Bad Request)} if the sectie is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sectie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/secties/{id}")
    public ResponseEntity<Sectie> updateSectie(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Sectie sectie
    ) throws URISyntaxException {
        log.debug("REST request to update Sectie : {}, {}", id, sectie);
        if (sectie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sectie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sectieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Sectie result = sectieRepository.save(sectie);
        sectieSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sectie.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /secties/:id} : Partial updates given fields of an existing sectie, field will ignore if it is null
     *
     * @param id the id of the sectie to save.
     * @param sectie the sectie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sectie,
     * or with status {@code 400 (Bad Request)} if the sectie is not valid,
     * or with status {@code 404 (Not Found)} if the sectie is not found,
     * or with status {@code 500 (Internal Server Error)} if the sectie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/secties/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Sectie> partialUpdateSectie(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Sectie sectie
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sectie partially : {}, {}", id, sectie);
        if (sectie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sectie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sectieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sectie> result = sectieRepository
            .findById(sectie.getId())
            .map(
                existingSectie -> {
                    if (sectie.getSectieId() != null) {
                        existingSectie.setSectieId(sectie.getSectieId());
                    }
                    if (sectie.getNume() != null) {
                        existingSectie.setNume(sectie.getNume());
                    }
                    if (sectie.getSefId() != null) {
                        existingSectie.setSefId(sectie.getSefId());
                    }
                    if (sectie.getTag() != null) {
                        existingSectie.setTag(sectie.getTag());
                    }
                    if (sectie.getNrPaturi() != null) {
                        existingSectie.setNrPaturi(sectie.getNrPaturi());
                    }

                    return existingSectie;
                }
            )
            .map(sectieRepository::save)
            .map(
                savedSectie -> {
                    sectieSearchRepository.save(savedSectie);

                    return savedSectie;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sectie.getId().toString())
        );
    }

    /**
     * {@code GET  /secties} : get all the secties.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of secties in body.
     */
    @GetMapping("/secties")
    public ResponseEntity<List<Sectie>> getAllSecties(Pageable pageable, @RequestParam(required = false) String filter) {
        if ("sefid-is-null".equals(filter)) {
            log.debug("REST request to get all Secties where sefId is null");
            return new ResponseEntity<>(
                StreamSupport
                    .stream(sectieRepository.findAll().spliterator(), false)
                    .filter(sectie -> sectie.getSefId() == null)
                    .collect(Collectors.toList()),
                HttpStatus.OK
            );
        }
        log.debug("REST request to get a page of Secties");
        Page<Sectie> page = sectieRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /secties/:id} : get the "id" sectie.
     *
     * @param id the id of the sectie to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sectie, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/secties/{id}")
    public ResponseEntity<Sectie> getSectie(@PathVariable Long id) {
        log.debug("REST request to get Sectie : {}", id);
        Optional<Sectie> sectie = sectieRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sectie);
    }

    /**
     * {@code DELETE  /secties/:id} : delete the "id" sectie.
     *
     * @param id the id of the sectie to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/secties/{id}")
    public ResponseEntity<Void> deleteSectie(@PathVariable Long id) {
        log.debug("REST request to delete Sectie : {}", id);
        sectieRepository.deleteById(id);
        sectieSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/secties?query=:query} : search for the sectie corresponding
     * to the query.
     *
     * @param query the query of the sectie search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/secties")
    public ResponseEntity<List<Sectie>> searchSecties(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Secties for query {}", query);
        Page<Sectie> page = sectieSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
