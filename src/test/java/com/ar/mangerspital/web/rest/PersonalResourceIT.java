package com.ar.mangerspital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ar.mangerspital.IntegrationTest;
import com.ar.mangerspital.domain.Personal;
import com.ar.mangerspital.domain.enumeration.TagPersonal;
import com.ar.mangerspital.repository.PersonalRepository;
import com.ar.mangerspital.repository.search.PersonalSearchRepository;
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
 * Integration tests for the {@link PersonalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PersonalResourceIT {

    private static final Long DEFAULT_PACIENT_ID = 1L;
    private static final Long UPDATED_PACIENT_ID = 2L;

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final String DEFAULT_PRENUME = "AAAAAAAAAA";
    private static final String UPDATED_PRENUME = "BBBBBBBBBB";

    private static final String DEFAULT_SECTIE_ID = "AAAAAAAAAA";
    private static final String UPDATED_SECTIE_ID = "BBBBBBBBBB";

    private static final TagPersonal DEFAULT_TAG = TagPersonal.TESA;
    private static final TagPersonal UPDATED_TAG = TagPersonal.PSA;

    private static final String ENTITY_API_URL = "/api/personals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/personals";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PersonalRepository personalRepository;

    /**
     * This repository is mocked in the com.ar.mangerspital.repository.search test package.
     *
     * @see com.ar.mangerspital.repository.search.PersonalSearchRepositoryMockConfiguration
     */
    @Autowired
    private PersonalSearchRepository mockPersonalSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPersonalMockMvc;

    private Personal personal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Personal createEntity(EntityManager em) {
        Personal personal = new Personal()
            .pacientId(DEFAULT_PACIENT_ID)
            .nume(DEFAULT_NUME)
            .prenume(DEFAULT_PRENUME)
            .sectieId(DEFAULT_SECTIE_ID)
            .tag(DEFAULT_TAG);
        return personal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Personal createUpdatedEntity(EntityManager em) {
        Personal personal = new Personal()
            .pacientId(UPDATED_PACIENT_ID)
            .nume(UPDATED_NUME)
            .prenume(UPDATED_PRENUME)
            .sectieId(UPDATED_SECTIE_ID)
            .tag(UPDATED_TAG);
        return personal;
    }

    @BeforeEach
    public void initTest() {
        personal = createEntity(em);
    }

    @Test
    @Transactional
    void createPersonal() throws Exception {
        int databaseSizeBeforeCreate = personalRepository.findAll().size();
        // Create the Personal
        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isCreated());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeCreate + 1);
        Personal testPersonal = personalList.get(personalList.size() - 1);
        assertThat(testPersonal.getPacientId()).isEqualTo(DEFAULT_PACIENT_ID);
        assertThat(testPersonal.getNume()).isEqualTo(DEFAULT_NUME);
        assertThat(testPersonal.getPrenume()).isEqualTo(DEFAULT_PRENUME);
        assertThat(testPersonal.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);
        assertThat(testPersonal.getTag()).isEqualTo(DEFAULT_TAG);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(1)).save(testPersonal);
    }

    @Test
    @Transactional
    void createPersonalWithExistingId() throws Exception {
        // Create the Personal with an existing ID
        personal.setId(1L);

        int databaseSizeBeforeCreate = personalRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeCreate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void checkPacientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = personalRepository.findAll().size();
        // set the field null
        personal.setPacientId(null);

        // Create the Personal, which fails.

        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = personalRepository.findAll().size();
        // set the field null
        personal.setNume(null);

        // Create the Personal, which fails.

        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = personalRepository.findAll().size();
        // set the field null
        personal.setPrenume(null);

        // Create the Personal, which fails.

        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSectieIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = personalRepository.findAll().size();
        // set the field null
        personal.setSectieId(null);

        // Create the Personal, which fails.

        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTagIsRequired() throws Exception {
        int databaseSizeBeforeTest = personalRepository.findAll().size();
        // set the field null
        personal.setTag(null);

        // Create the Personal, which fails.

        restPersonalMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPersonals() throws Exception {
        // Initialize the database
        personalRepository.saveAndFlush(personal);

        // Get all the personalList
        restPersonalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personal.getId().intValue())))
            .andExpect(jsonPath("$.[*].pacientId").value(hasItem(DEFAULT_PACIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].prenume").value(hasItem(DEFAULT_PRENUME)))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())));
    }

    @Test
    @Transactional
    void getPersonal() throws Exception {
        // Initialize the database
        personalRepository.saveAndFlush(personal);

        // Get the personal
        restPersonalMockMvc
            .perform(get(ENTITY_API_URL_ID, personal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(personal.getId().intValue()))
            .andExpect(jsonPath("$.pacientId").value(DEFAULT_PACIENT_ID.intValue()))
            .andExpect(jsonPath("$.nume").value(DEFAULT_NUME))
            .andExpect(jsonPath("$.prenume").value(DEFAULT_PRENUME))
            .andExpect(jsonPath("$.sectieId").value(DEFAULT_SECTIE_ID))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPersonal() throws Exception {
        // Get the personal
        restPersonalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPersonal() throws Exception {
        // Initialize the database
        personalRepository.saveAndFlush(personal);

        int databaseSizeBeforeUpdate = personalRepository.findAll().size();

        // Update the personal
        Personal updatedPersonal = personalRepository.findById(personal.getId()).get();
        // Disconnect from session so that the updates on updatedPersonal are not directly saved in db
        em.detach(updatedPersonal);
        updatedPersonal
            .pacientId(UPDATED_PACIENT_ID)
            .nume(UPDATED_NUME)
            .prenume(UPDATED_PRENUME)
            .sectieId(UPDATED_SECTIE_ID)
            .tag(UPDATED_TAG);

        restPersonalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPersonal.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPersonal))
            )
            .andExpect(status().isOk());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);
        Personal testPersonal = personalList.get(personalList.size() - 1);
        assertThat(testPersonal.getPacientId()).isEqualTo(UPDATED_PACIENT_ID);
        assertThat(testPersonal.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testPersonal.getPrenume()).isEqualTo(UPDATED_PRENUME);
        assertThat(testPersonal.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testPersonal.getTag()).isEqualTo(UPDATED_TAG);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository).save(testPersonal);
    }

    @Test
    @Transactional
    void putNonExistingPersonal() throws Exception {
        int databaseSizeBeforeUpdate = personalRepository.findAll().size();
        personal.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personal.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void putWithIdMismatchPersonal() throws Exception {
        int databaseSizeBeforeUpdate = personalRepository.findAll().size();
        personal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPersonal() throws Exception {
        int databaseSizeBeforeUpdate = personalRepository.findAll().size();
        personal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void partialUpdatePersonalWithPatch() throws Exception {
        // Initialize the database
        personalRepository.saveAndFlush(personal);

        int databaseSizeBeforeUpdate = personalRepository.findAll().size();

        // Update the personal using partial update
        Personal partialUpdatedPersonal = new Personal();
        partialUpdatedPersonal.setId(personal.getId());

        restPersonalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonal.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonal))
            )
            .andExpect(status().isOk());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);
        Personal testPersonal = personalList.get(personalList.size() - 1);
        assertThat(testPersonal.getPacientId()).isEqualTo(DEFAULT_PACIENT_ID);
        assertThat(testPersonal.getNume()).isEqualTo(DEFAULT_NUME);
        assertThat(testPersonal.getPrenume()).isEqualTo(DEFAULT_PRENUME);
        assertThat(testPersonal.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);
        assertThat(testPersonal.getTag()).isEqualTo(DEFAULT_TAG);
    }

    @Test
    @Transactional
    void fullUpdatePersonalWithPatch() throws Exception {
        // Initialize the database
        personalRepository.saveAndFlush(personal);

        int databaseSizeBeforeUpdate = personalRepository.findAll().size();

        // Update the personal using partial update
        Personal partialUpdatedPersonal = new Personal();
        partialUpdatedPersonal.setId(personal.getId());

        partialUpdatedPersonal
            .pacientId(UPDATED_PACIENT_ID)
            .nume(UPDATED_NUME)
            .prenume(UPDATED_PRENUME)
            .sectieId(UPDATED_SECTIE_ID)
            .tag(UPDATED_TAG);

        restPersonalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonal.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonal))
            )
            .andExpect(status().isOk());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);
        Personal testPersonal = personalList.get(personalList.size() - 1);
        assertThat(testPersonal.getPacientId()).isEqualTo(UPDATED_PACIENT_ID);
        assertThat(testPersonal.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testPersonal.getPrenume()).isEqualTo(UPDATED_PRENUME);
        assertThat(testPersonal.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testPersonal.getTag()).isEqualTo(UPDATED_TAG);
    }

    @Test
    @Transactional
    void patchNonExistingPersonal() throws Exception {
        int databaseSizeBeforeUpdate = personalRepository.findAll().size();
        personal.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personal.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPersonal() throws Exception {
        int databaseSizeBeforeUpdate = personalRepository.findAll().size();
        personal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPersonal() throws Exception {
        int databaseSizeBeforeUpdate = personalRepository.findAll().size();
        personal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personal))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Personal in the database
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(0)).save(personal);
    }

    @Test
    @Transactional
    void deletePersonal() throws Exception {
        // Initialize the database
        personalRepository.saveAndFlush(personal);

        int databaseSizeBeforeDelete = personalRepository.findAll().size();

        // Delete the personal
        restPersonalMockMvc
            .perform(delete(ENTITY_API_URL_ID, personal.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Personal> personalList = personalRepository.findAll();
        assertThat(personalList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Personal in Elasticsearch
        verify(mockPersonalSearchRepository, times(1)).deleteById(personal.getId());
    }

    @Test
    @Transactional
    void searchPersonal() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        personalRepository.saveAndFlush(personal);
        when(mockPersonalSearchRepository.search(queryStringQuery("id:" + personal.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(personal), PageRequest.of(0, 1), 1));

        // Search the personal
        restPersonalMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + personal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personal.getId().intValue())))
            .andExpect(jsonPath("$.[*].pacientId").value(hasItem(DEFAULT_PACIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].prenume").value(hasItem(DEFAULT_PRENUME)))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())));
    }
}
