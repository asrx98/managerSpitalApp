package com.ar.mangerspital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ar.mangerspital.IntegrationTest;
import com.ar.mangerspital.domain.Inventar;
import com.ar.mangerspital.domain.enumeration.TagInventar;
import com.ar.mangerspital.repository.InventarRepository;
import com.ar.mangerspital.repository.search.InventarSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InventarResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventarResourceIT {

    private static final Long DEFAULT_INVENTAR_ID = 1L;
    private static final Long UPDATED_INVENTAR_ID = 2L;

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final Integer DEFAULT_STOC = 1;
    private static final Integer UPDATED_STOC = 2;

    private static final TagInventar DEFAULT_TAG = TagInventar.MEDICAMENT;
    private static final TagInventar UPDATED_TAG = TagInventar.APARATURA;

    private static final String ENTITY_API_URL = "/api/inventars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/inventars";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InventarRepository inventarRepository;

    /**
     * This repository is mocked in the com.ar.mangerspital.repository.search test package.
     *
     * @see com.ar.mangerspital.repository.search.InventarSearchRepositoryMockConfiguration
     */
    @Autowired
    private InventarSearchRepository mockInventarSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventarMockMvc;

    private Inventar inventar;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventar createEntity(EntityManager em) {
        Inventar inventar = new Inventar().inventarId(DEFAULT_INVENTAR_ID).nume(DEFAULT_NUME).stoc(DEFAULT_STOC).tag(DEFAULT_TAG);
        return inventar;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventar createUpdatedEntity(EntityManager em) {
        Inventar inventar = new Inventar().inventarId(UPDATED_INVENTAR_ID).nume(UPDATED_NUME).stoc(UPDATED_STOC).tag(UPDATED_TAG);
        return inventar;
    }

    @BeforeEach
    public void initTest() {
        inventar = createEntity(em);
    }

    @Test
    @Transactional
    void createInventar() throws Exception {
        int databaseSizeBeforeCreate = inventarRepository.findAll().size();
        // Create the Inventar
        restInventarMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isCreated());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeCreate + 1);
        Inventar testInventar = inventarList.get(inventarList.size() - 1);
        assertThat(testInventar.getInventarId()).isEqualTo(DEFAULT_INVENTAR_ID);
        assertThat(testInventar.getNume()).isEqualTo(DEFAULT_NUME);
        assertThat(testInventar.getStoc()).isEqualTo(DEFAULT_STOC);
        assertThat(testInventar.getTag()).isEqualTo(DEFAULT_TAG);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(1)).save(testInventar);
    }

    @Test
    @Transactional
    void createInventarWithExistingId() throws Exception {
        // Create the Inventar with an existing ID
        inventar.setId(1L);

        int databaseSizeBeforeCreate = inventarRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventarMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeCreate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void checkInventarIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventarRepository.findAll().size();
        // set the field null
        inventar.setInventarId(null);

        // Create the Inventar, which fails.

        restInventarMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventarRepository.findAll().size();
        // set the field null
        inventar.setNume(null);

        // Create the Inventar, which fails.

        restInventarMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStocIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventarRepository.findAll().size();
        // set the field null
        inventar.setStoc(null);

        // Create the Inventar, which fails.

        restInventarMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTagIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventarRepository.findAll().size();
        // set the field null
        inventar.setTag(null);

        // Create the Inventar, which fails.

        restInventarMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventars() throws Exception {
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);

        // Get all the inventarList
        restInventarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventar.getId().intValue())))
            .andExpect(jsonPath("$.[*].inventarId").value(hasItem(DEFAULT_INVENTAR_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].stoc").value(hasItem(DEFAULT_STOC)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())));
    }

    @Test
    @Transactional
    void getInventar() throws Exception {
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);

        // Get the inventar
        restInventarMockMvc
            .perform(get(ENTITY_API_URL_ID, inventar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventar.getId().intValue()))
            .andExpect(jsonPath("$.inventarId").value(DEFAULT_INVENTAR_ID.intValue()))
            .andExpect(jsonPath("$.nume").value(DEFAULT_NUME))
            .andExpect(jsonPath("$.stoc").value(DEFAULT_STOC))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG.toString()));
    }

    @Test
    @Transactional
    void getNonExistingInventar() throws Exception {
        // Get the inventar
        restInventarMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewInventar() throws Exception {
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);

        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();

        // Update the inventar
        Inventar updatedInventar = inventarRepository.findById(inventar.getId()).get();
        // Disconnect from session so that the updates on updatedInventar are not directly saved in db
        em.detach(updatedInventar);
        updatedInventar.inventarId(UPDATED_INVENTAR_ID).nume(UPDATED_NUME).stoc(UPDATED_STOC).tag(UPDATED_TAG);

        restInventarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInventar.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInventar))
            )
            .andExpect(status().isOk());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);
        Inventar testInventar = inventarList.get(inventarList.size() - 1);
        assertThat(testInventar.getInventarId()).isEqualTo(UPDATED_INVENTAR_ID);
        assertThat(testInventar.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testInventar.getStoc()).isEqualTo(UPDATED_STOC);
        assertThat(testInventar.getTag()).isEqualTo(UPDATED_TAG);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository).save(testInventar);
    }

    @Test
    @Transactional
    void putNonExistingInventar() throws Exception {
        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();
        inventar.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventar.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventar() throws Exception {
        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();
        inventar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventar() throws Exception {
        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();
        inventar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventarMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void partialUpdateInventarWithPatch() throws Exception {
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);

        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();

        // Update the inventar using partial update
        Inventar partialUpdatedInventar = new Inventar();
        partialUpdatedInventar.setId(inventar.getId());

        partialUpdatedInventar.inventarId(UPDATED_INVENTAR_ID).nume(UPDATED_NUME);

        restInventarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventar.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventar))
            )
            .andExpect(status().isOk());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);
        Inventar testInventar = inventarList.get(inventarList.size() - 1);
        assertThat(testInventar.getInventarId()).isEqualTo(UPDATED_INVENTAR_ID);
        assertThat(testInventar.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testInventar.getStoc()).isEqualTo(DEFAULT_STOC);
        assertThat(testInventar.getTag()).isEqualTo(DEFAULT_TAG);
    }

    @Test
    @Transactional
    void fullUpdateInventarWithPatch() throws Exception {
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);

        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();

        // Update the inventar using partial update
        Inventar partialUpdatedInventar = new Inventar();
        partialUpdatedInventar.setId(inventar.getId());

        partialUpdatedInventar.inventarId(UPDATED_INVENTAR_ID).nume(UPDATED_NUME).stoc(UPDATED_STOC).tag(UPDATED_TAG);

        restInventarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventar.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventar))
            )
            .andExpect(status().isOk());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);
        Inventar testInventar = inventarList.get(inventarList.size() - 1);
        assertThat(testInventar.getInventarId()).isEqualTo(UPDATED_INVENTAR_ID);
        assertThat(testInventar.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testInventar.getStoc()).isEqualTo(UPDATED_STOC);
        assertThat(testInventar.getTag()).isEqualTo(UPDATED_TAG);
    }

    @Test
    @Transactional
    void patchNonExistingInventar() throws Exception {
        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();
        inventar.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventar.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventar() throws Exception {
        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();
        inventar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventar() throws Exception {
        int databaseSizeBeforeUpdate = inventarRepository.findAll().size();
        inventar.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventarMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inventar))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventar in the database
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(0)).save(inventar);
    }

    @Test
    @Transactional
    void deleteInventar() throws Exception {
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);

        int databaseSizeBeforeDelete = inventarRepository.findAll().size();

        // Delete the inventar
        restInventarMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventar.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Inventar> inventarList = inventarRepository.findAll();
        assertThat(inventarList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Inventar in Elasticsearch
        verify(mockInventarSearchRepository, times(1)).deleteById(inventar.getId());
    }

    @Test
    @Transactional
    void searchInventar() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        inventarRepository.saveAndFlush(inventar);
        when(mockInventarSearchRepository.search(queryStringQuery("id:" + inventar.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(inventar), PageRequest.of(0, 1), 1));

        // Search the inventar
        restInventarMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + inventar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventar.getId().intValue())))
            .andExpect(jsonPath("$.[*].inventarId").value(hasItem(DEFAULT_INVENTAR_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].stoc").value(hasItem(DEFAULT_STOC)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())));
    }
}
