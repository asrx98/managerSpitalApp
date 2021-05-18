package com.ar.mangerspital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ar.mangerspital.IntegrationTest;
import com.ar.mangerspital.domain.Sectie;
import com.ar.mangerspital.domain.enumeration.TagSectie;
import com.ar.mangerspital.repository.SectieRepository;
import com.ar.mangerspital.repository.search.SectieSearchRepository;
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
 * Integration tests for the {@link SectieResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SectieResourceIT {

    private static final Long DEFAULT_SECTIE_ID = 1L;
    private static final Long UPDATED_SECTIE_ID = 2L;

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final String DEFAULT_SEF_ID = "AAAAAAAAAA";
    private static final String UPDATED_SEF_ID = "BBBBBBBBBB";

    private static final TagSectie DEFAULT_TAG = TagSectie.TESA;
    private static final TagSectie UPDATED_TAG = TagSectie.SPITAL;

    private static final Integer DEFAULT_NR_PATURI = 1;
    private static final Integer UPDATED_NR_PATURI = 2;

    private static final String ENTITY_API_URL = "/api/secties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/secties";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SectieRepository sectieRepository;

    /**
     * This repository is mocked in the com.ar.mangerspital.repository.search test package.
     *
     * @see com.ar.mangerspital.repository.search.SectieSearchRepositoryMockConfiguration
     */
    @Autowired
    private SectieSearchRepository mockSectieSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSectieMockMvc;

    private Sectie sectie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sectie createEntity(EntityManager em) {
        Sectie sectie = new Sectie()
            .sectieId(DEFAULT_SECTIE_ID)
            .nume(DEFAULT_NUME)
            .sefId(DEFAULT_SEF_ID)
            .tag(DEFAULT_TAG)
            .nrPaturi(DEFAULT_NR_PATURI);
        return sectie;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sectie createUpdatedEntity(EntityManager em) {
        Sectie sectie = new Sectie()
            .sectieId(UPDATED_SECTIE_ID)
            .nume(UPDATED_NUME)
            .sefId(UPDATED_SEF_ID)
            .tag(UPDATED_TAG)
            .nrPaturi(UPDATED_NR_PATURI);
        return sectie;
    }

    @BeforeEach
    public void initTest() {
        sectie = createEntity(em);
    }

    @Test
    @Transactional
    void createSectie() throws Exception {
        int databaseSizeBeforeCreate = sectieRepository.findAll().size();
        // Create the Sectie
        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isCreated());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeCreate + 1);
        Sectie testSectie = sectieList.get(sectieList.size() - 1);
        assertThat(testSectie.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);
        assertThat(testSectie.getNume()).isEqualTo(DEFAULT_NUME);
        assertThat(testSectie.getSefId()).isEqualTo(DEFAULT_SEF_ID);
        assertThat(testSectie.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testSectie.getNrPaturi()).isEqualTo(DEFAULT_NR_PATURI);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(1)).save(testSectie);
    }

    @Test
    @Transactional
    void createSectieWithExistingId() throws Exception {
        // Create the Sectie with an existing ID
        sectie.setId(1L);

        int databaseSizeBeforeCreate = sectieRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void checkSectieIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectieRepository.findAll().size();
        // set the field null
        sectie.setSectieId(null);

        // Create the Sectie, which fails.

        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectieRepository.findAll().size();
        // set the field null
        sectie.setNume(null);

        // Create the Sectie, which fails.

        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSefIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectieRepository.findAll().size();
        // set the field null
        sectie.setSefId(null);

        // Create the Sectie, which fails.

        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTagIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectieRepository.findAll().size();
        // set the field null
        sectie.setTag(null);

        // Create the Sectie, which fails.

        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNrPaturiIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectieRepository.findAll().size();
        // set the field null
        sectie.setNrPaturi(null);

        // Create the Sectie, which fails.

        restSectieMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSecties() throws Exception {
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);

        // Get all the sectieList
        restSectieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sectie.getId().intValue())))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].sefId").value(hasItem(DEFAULT_SEF_ID)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].nrPaturi").value(hasItem(DEFAULT_NR_PATURI)));
    }

    @Test
    @Transactional
    void getSectie() throws Exception {
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);

        // Get the sectie
        restSectieMockMvc
            .perform(get(ENTITY_API_URL_ID, sectie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sectie.getId().intValue()))
            .andExpect(jsonPath("$.sectieId").value(DEFAULT_SECTIE_ID.intValue()))
            .andExpect(jsonPath("$.nume").value(DEFAULT_NUME))
            .andExpect(jsonPath("$.sefId").value(DEFAULT_SEF_ID))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG.toString()))
            .andExpect(jsonPath("$.nrPaturi").value(DEFAULT_NR_PATURI));
    }

    @Test
    @Transactional
    void getNonExistingSectie() throws Exception {
        // Get the sectie
        restSectieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSectie() throws Exception {
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);

        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();

        // Update the sectie
        Sectie updatedSectie = sectieRepository.findById(sectie.getId()).get();
        // Disconnect from session so that the updates on updatedSectie are not directly saved in db
        em.detach(updatedSectie);
        updatedSectie.sectieId(UPDATED_SECTIE_ID).nume(UPDATED_NUME).sefId(UPDATED_SEF_ID).tag(UPDATED_TAG).nrPaturi(UPDATED_NR_PATURI);

        restSectieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSectie.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSectie))
            )
            .andExpect(status().isOk());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);
        Sectie testSectie = sectieList.get(sectieList.size() - 1);
        assertThat(testSectie.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testSectie.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testSectie.getSefId()).isEqualTo(UPDATED_SEF_ID);
        assertThat(testSectie.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testSectie.getNrPaturi()).isEqualTo(UPDATED_NR_PATURI);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository).save(testSectie);
    }

    @Test
    @Transactional
    void putNonExistingSectie() throws Exception {
        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();
        sectie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSectieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sectie.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void putWithIdMismatchSectie() throws Exception {
        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();
        sectie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSectie() throws Exception {
        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();
        sectie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectieMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void partialUpdateSectieWithPatch() throws Exception {
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);

        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();

        // Update the sectie using partial update
        Sectie partialUpdatedSectie = new Sectie();
        partialUpdatedSectie.setId(sectie.getId());

        partialUpdatedSectie.sectieId(UPDATED_SECTIE_ID).nume(UPDATED_NUME).sefId(UPDATED_SEF_ID);

        restSectieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSectie.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSectie))
            )
            .andExpect(status().isOk());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);
        Sectie testSectie = sectieList.get(sectieList.size() - 1);
        assertThat(testSectie.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testSectie.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testSectie.getSefId()).isEqualTo(UPDATED_SEF_ID);
        assertThat(testSectie.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testSectie.getNrPaturi()).isEqualTo(DEFAULT_NR_PATURI);
    }

    @Test
    @Transactional
    void fullUpdateSectieWithPatch() throws Exception {
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);

        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();

        // Update the sectie using partial update
        Sectie partialUpdatedSectie = new Sectie();
        partialUpdatedSectie.setId(sectie.getId());

        partialUpdatedSectie
            .sectieId(UPDATED_SECTIE_ID)
            .nume(UPDATED_NUME)
            .sefId(UPDATED_SEF_ID)
            .tag(UPDATED_TAG)
            .nrPaturi(UPDATED_NR_PATURI);

        restSectieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSectie.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSectie))
            )
            .andExpect(status().isOk());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);
        Sectie testSectie = sectieList.get(sectieList.size() - 1);
        assertThat(testSectie.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testSectie.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testSectie.getSefId()).isEqualTo(UPDATED_SEF_ID);
        assertThat(testSectie.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testSectie.getNrPaturi()).isEqualTo(UPDATED_NR_PATURI);
    }

    @Test
    @Transactional
    void patchNonExistingSectie() throws Exception {
        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();
        sectie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSectieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sectie.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSectie() throws Exception {
        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();
        sectie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSectie() throws Exception {
        int databaseSizeBeforeUpdate = sectieRepository.findAll().size();
        sectie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectieMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sectie))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sectie in the database
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(0)).save(sectie);
    }

    @Test
    @Transactional
    void deleteSectie() throws Exception {
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);

        int databaseSizeBeforeDelete = sectieRepository.findAll().size();

        // Delete the sectie
        restSectieMockMvc
            .perform(delete(ENTITY_API_URL_ID, sectie.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sectie> sectieList = sectieRepository.findAll();
        assertThat(sectieList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sectie in Elasticsearch
        verify(mockSectieSearchRepository, times(1)).deleteById(sectie.getId());
    }

    @Test
    @Transactional
    void searchSectie() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        sectieRepository.saveAndFlush(sectie);
        when(mockSectieSearchRepository.search(queryStringQuery("id:" + sectie.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(sectie), PageRequest.of(0, 1), 1));

        // Search the sectie
        restSectieMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sectie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sectie.getId().intValue())))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].sefId").value(hasItem(DEFAULT_SEF_ID)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].nrPaturi").value(hasItem(DEFAULT_NR_PATURI)));
    }
}
