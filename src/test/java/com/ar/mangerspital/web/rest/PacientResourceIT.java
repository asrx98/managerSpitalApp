package com.ar.mangerspital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ar.mangerspital.IntegrationTest;
import com.ar.mangerspital.domain.Pacient;
import com.ar.mangerspital.repository.PacientRepository;
import com.ar.mangerspital.repository.search.PacientSearchRepository;
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
 * Integration tests for the {@link PacientResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PacientResourceIT {

    private static final Long DEFAULT_PACIENT_ID = 1L;
    private static final Long UPDATED_PACIENT_ID = 2L;

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final String DEFAULT_PRENUME = "AAAAAAAAAA";
    private static final String UPDATED_PRENUME = "BBBBBBBBBB";

    private static final String DEFAULT_SECTIE_ID = "AAAAAAAAAA";
    private static final String UPDATED_SECTIE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SALON_ID = "AAAAAAAAAA";
    private static final String UPDATED_SALON_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pacients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/pacients";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PacientRepository pacientRepository;

    /**
     * This repository is mocked in the com.ar.mangerspital.repository.search test package.
     *
     * @see com.ar.mangerspital.repository.search.PacientSearchRepositoryMockConfiguration
     */
    @Autowired
    private PacientSearchRepository mockPacientSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPacientMockMvc;

    private Pacient pacient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pacient createEntity(EntityManager em) {
        Pacient pacient = new Pacient()
            .pacientId(DEFAULT_PACIENT_ID)
            .nume(DEFAULT_NUME)
            .prenume(DEFAULT_PRENUME)
            .sectieId(DEFAULT_SECTIE_ID)
            .salonId(DEFAULT_SALON_ID);
        return pacient;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pacient createUpdatedEntity(EntityManager em) {
        Pacient pacient = new Pacient()
            .pacientId(UPDATED_PACIENT_ID)
            .nume(UPDATED_NUME)
            .prenume(UPDATED_PRENUME)
            .sectieId(UPDATED_SECTIE_ID)
            .salonId(UPDATED_SALON_ID);
        return pacient;
    }

    @BeforeEach
    public void initTest() {
        pacient = createEntity(em);
    }

    @Test
    @Transactional
    void createPacient() throws Exception {
        int databaseSizeBeforeCreate = pacientRepository.findAll().size();
        // Create the Pacient
        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isCreated());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeCreate + 1);
        Pacient testPacient = pacientList.get(pacientList.size() - 1);
        assertThat(testPacient.getPacientId()).isEqualTo(DEFAULT_PACIENT_ID);
        assertThat(testPacient.getNume()).isEqualTo(DEFAULT_NUME);
        assertThat(testPacient.getPrenume()).isEqualTo(DEFAULT_PRENUME);
        assertThat(testPacient.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);
        assertThat(testPacient.getSalonId()).isEqualTo(DEFAULT_SALON_ID);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(1)).save(testPacient);
    }

    @Test
    @Transactional
    void createPacientWithExistingId() throws Exception {
        // Create the Pacient with an existing ID
        pacient.setId(1L);

        int databaseSizeBeforeCreate = pacientRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeCreate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void checkPacientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = pacientRepository.findAll().size();
        // set the field null
        pacient.setPacientId(null);

        // Create the Pacient, which fails.

        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pacientRepository.findAll().size();
        // set the field null
        pacient.setNume(null);

        // Create the Pacient, which fails.

        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pacientRepository.findAll().size();
        // set the field null
        pacient.setPrenume(null);

        // Create the Pacient, which fails.

        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSectieIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = pacientRepository.findAll().size();
        // set the field null
        pacient.setSectieId(null);

        // Create the Pacient, which fails.

        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSalonIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = pacientRepository.findAll().size();
        // set the field null
        pacient.setSalonId(null);

        // Create the Pacient, which fails.

        restPacientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPacients() throws Exception {
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);

        // Get all the pacientList
        restPacientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pacient.getId().intValue())))
            .andExpect(jsonPath("$.[*].pacientId").value(hasItem(DEFAULT_PACIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].prenume").value(hasItem(DEFAULT_PRENUME)))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID)))
            .andExpect(jsonPath("$.[*].salonId").value(hasItem(DEFAULT_SALON_ID)));
    }

    @Test
    @Transactional
    void getPacient() throws Exception {
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);

        // Get the pacient
        restPacientMockMvc
            .perform(get(ENTITY_API_URL_ID, pacient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pacient.getId().intValue()))
            .andExpect(jsonPath("$.pacientId").value(DEFAULT_PACIENT_ID.intValue()))
            .andExpect(jsonPath("$.nume").value(DEFAULT_NUME))
            .andExpect(jsonPath("$.prenume").value(DEFAULT_PRENUME))
            .andExpect(jsonPath("$.sectieId").value(DEFAULT_SECTIE_ID))
            .andExpect(jsonPath("$.salonId").value(DEFAULT_SALON_ID));
    }

    @Test
    @Transactional
    void getNonExistingPacient() throws Exception {
        // Get the pacient
        restPacientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPacient() throws Exception {
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);

        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();

        // Update the pacient
        Pacient updatedPacient = pacientRepository.findById(pacient.getId()).get();
        // Disconnect from session so that the updates on updatedPacient are not directly saved in db
        em.detach(updatedPacient);
        updatedPacient
            .pacientId(UPDATED_PACIENT_ID)
            .nume(UPDATED_NUME)
            .prenume(UPDATED_PRENUME)
            .sectieId(UPDATED_SECTIE_ID)
            .salonId(UPDATED_SALON_ID);

        restPacientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPacient.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPacient))
            )
            .andExpect(status().isOk());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);
        Pacient testPacient = pacientList.get(pacientList.size() - 1);
        assertThat(testPacient.getPacientId()).isEqualTo(UPDATED_PACIENT_ID);
        assertThat(testPacient.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testPacient.getPrenume()).isEqualTo(UPDATED_PRENUME);
        assertThat(testPacient.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testPacient.getSalonId()).isEqualTo(UPDATED_SALON_ID);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository).save(testPacient);
    }

    @Test
    @Transactional
    void putNonExistingPacient() throws Exception {
        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();
        pacient.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPacientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pacient.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void putWithIdMismatchPacient() throws Exception {
        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();
        pacient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPacientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPacient() throws Exception {
        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();
        pacient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPacientMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void partialUpdatePacientWithPatch() throws Exception {
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);

        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();

        // Update the pacient using partial update
        Pacient partialUpdatedPacient = new Pacient();
        partialUpdatedPacient.setId(pacient.getId());

        partialUpdatedPacient.nume(UPDATED_NUME).salonId(UPDATED_SALON_ID);

        restPacientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPacient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPacient))
            )
            .andExpect(status().isOk());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);
        Pacient testPacient = pacientList.get(pacientList.size() - 1);
        assertThat(testPacient.getPacientId()).isEqualTo(DEFAULT_PACIENT_ID);
        assertThat(testPacient.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testPacient.getPrenume()).isEqualTo(DEFAULT_PRENUME);
        assertThat(testPacient.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);
        assertThat(testPacient.getSalonId()).isEqualTo(UPDATED_SALON_ID);
    }

    @Test
    @Transactional
    void fullUpdatePacientWithPatch() throws Exception {
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);

        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();

        // Update the pacient using partial update
        Pacient partialUpdatedPacient = new Pacient();
        partialUpdatedPacient.setId(pacient.getId());

        partialUpdatedPacient
            .pacientId(UPDATED_PACIENT_ID)
            .nume(UPDATED_NUME)
            .prenume(UPDATED_PRENUME)
            .sectieId(UPDATED_SECTIE_ID)
            .salonId(UPDATED_SALON_ID);

        restPacientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPacient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPacient))
            )
            .andExpect(status().isOk());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);
        Pacient testPacient = pacientList.get(pacientList.size() - 1);
        assertThat(testPacient.getPacientId()).isEqualTo(UPDATED_PACIENT_ID);
        assertThat(testPacient.getNume()).isEqualTo(UPDATED_NUME);
        assertThat(testPacient.getPrenume()).isEqualTo(UPDATED_PRENUME);
        assertThat(testPacient.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
        assertThat(testPacient.getSalonId()).isEqualTo(UPDATED_SALON_ID);
    }

    @Test
    @Transactional
    void patchNonExistingPacient() throws Exception {
        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();
        pacient.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPacientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pacient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPacient() throws Exception {
        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();
        pacient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPacientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPacient() throws Exception {
        int databaseSizeBeforeUpdate = pacientRepository.findAll().size();
        pacient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPacientMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pacient))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pacient in the database
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(0)).save(pacient);
    }

    @Test
    @Transactional
    void deletePacient() throws Exception {
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);

        int databaseSizeBeforeDelete = pacientRepository.findAll().size();

        // Delete the pacient
        restPacientMockMvc
            .perform(delete(ENTITY_API_URL_ID, pacient.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pacient> pacientList = pacientRepository.findAll();
        assertThat(pacientList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Pacient in Elasticsearch
        verify(mockPacientSearchRepository, times(1)).deleteById(pacient.getId());
    }

    @Test
    @Transactional
    void searchPacient() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        pacientRepository.saveAndFlush(pacient);
        when(mockPacientSearchRepository.search(queryStringQuery("id:" + pacient.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(pacient), PageRequest.of(0, 1), 1));

        // Search the pacient
        restPacientMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + pacient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pacient.getId().intValue())))
            .andExpect(jsonPath("$.[*].pacientId").value(hasItem(DEFAULT_PACIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].nume").value(hasItem(DEFAULT_NUME)))
            .andExpect(jsonPath("$.[*].prenume").value(hasItem(DEFAULT_PRENUME)))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID)))
            .andExpect(jsonPath("$.[*].salonId").value(hasItem(DEFAULT_SALON_ID)));
    }
}
