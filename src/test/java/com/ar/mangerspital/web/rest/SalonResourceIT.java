package com.ar.mangerspital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ar.mangerspital.IntegrationTest;
import com.ar.mangerspital.domain.Salon;
import com.ar.mangerspital.repository.SalonRepository;
import com.ar.mangerspital.repository.search.SalonSearchRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SalonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalonResourceIT {

    private static final Long DEFAULT_SALON_ID = 1L;
    private static final Long UPDATED_SALON_ID = 2L;

    private static final Long DEFAULT_SECTIE_ID = 1L;
    private static final Long UPDATED_SECTIE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/salons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/salons";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalonRepository salonRepository;

    /**
     * This repository is mocked in the com.ar.mangerspital.repository.search test package.
     *
     * @see com.ar.mangerspital.repository.search.SalonSearchRepositoryMockConfiguration
     */
    @Autowired
    private SalonSearchRepository mockSalonSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalonMockMvc;

    private Salon salon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Salon createEntity(EntityManager em) {
        Salon salon = new Salon().salonId(DEFAULT_SALON_ID).sectieId(DEFAULT_SECTIE_ID);
        return salon;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Salon createUpdatedEntity(EntityManager em) {
        Salon salon = new Salon().salonId(UPDATED_SALON_ID).sectieId(UPDATED_SECTIE_ID);
        return salon;
    }

    @BeforeEach
    public void initTest() {
        salon = createEntity(em);
    }

    @Test
    @Transactional
    void createSalon() throws Exception {
        int databaseSizeBeforeCreate = salonRepository.findAll().size();
        // Create the Salon
        restSalonMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isCreated());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeCreate + 1);
        Salon testSalon = salonList.get(salonList.size() - 1);
        assertThat(testSalon.getSalonId()).isEqualTo(DEFAULT_SALON_ID);
        assertThat(testSalon.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(1)).save(testSalon);
    }

    @Test
    @Transactional
    void createSalonWithExistingId() throws Exception {
        // Create the Salon with an existing ID
        salon.setId(1L);

        int databaseSizeBeforeCreate = salonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalonMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeCreate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void getAllSalons() throws Exception {
        // Initialize the database
        salonRepository.saveAndFlush(salon);

        // Get all the salonList
        restSalonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salon.getId().intValue())))
            .andExpect(jsonPath("$.[*].salonId").value(hasItem(DEFAULT_SALON_ID.intValue())))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID.intValue())));
    }

    @Test
    @Transactional
    void getSalon() throws Exception {
        // Initialize the database
        salonRepository.saveAndFlush(salon);

        // Get the salon
        restSalonMockMvc
            .perform(get(ENTITY_API_URL_ID, salon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salon.getId().intValue()))
            .andExpect(jsonPath("$.salonId").value(DEFAULT_SALON_ID.intValue()))
            .andExpect(jsonPath("$.sectieId").value(DEFAULT_SECTIE_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSalon() throws Exception {
        // Get the salon
        restSalonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSalon() throws Exception {
        // Initialize the database
        salonRepository.saveAndFlush(salon);

        int databaseSizeBeforeUpdate = salonRepository.findAll().size();

        // Update the salon
        Salon updatedSalon = salonRepository.findById(salon.getId()).get();
        // Disconnect from session so that the updates on updatedSalon are not directly saved in db
        em.detach(updatedSalon);
        updatedSalon.salonId(UPDATED_SALON_ID).sectieId(UPDATED_SECTIE_ID);

        restSalonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSalon.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSalon))
            )
            .andExpect(status().isOk());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);
        Salon testSalon = salonList.get(salonList.size() - 1);
        assertThat(testSalon.getSalonId()).isEqualTo(UPDATED_SALON_ID);
        assertThat(testSalon.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository).save(testSalon);
    }

    @Test
    @Transactional
    void putNonExistingSalon() throws Exception {
        int databaseSizeBeforeUpdate = salonRepository.findAll().size();
        salon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salon.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalon() throws Exception {
        int databaseSizeBeforeUpdate = salonRepository.findAll().size();
        salon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalon() throws Exception {
        int databaseSizeBeforeUpdate = salonRepository.findAll().size();
        salon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalonMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void partialUpdateSalonWithPatch() throws Exception {
        // Initialize the database
        salonRepository.saveAndFlush(salon);

        int databaseSizeBeforeUpdate = salonRepository.findAll().size();

        // Update the salon using partial update
        Salon partialUpdatedSalon = new Salon();
        partialUpdatedSalon.setId(salon.getId());

        partialUpdatedSalon.salonId(UPDATED_SALON_ID);

        restSalonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalon.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalon))
            )
            .andExpect(status().isOk());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);
        Salon testSalon = salonList.get(salonList.size() - 1);
        assertThat(testSalon.getSalonId()).isEqualTo(UPDATED_SALON_ID);
        assertThat(testSalon.getSectieId()).isEqualTo(DEFAULT_SECTIE_ID);
    }

    @Test
    @Transactional
    void fullUpdateSalonWithPatch() throws Exception {
        // Initialize the database
        salonRepository.saveAndFlush(salon);

        int databaseSizeBeforeUpdate = salonRepository.findAll().size();

        // Update the salon using partial update
        Salon partialUpdatedSalon = new Salon();
        partialUpdatedSalon.setId(salon.getId());

        partialUpdatedSalon.salonId(UPDATED_SALON_ID).sectieId(UPDATED_SECTIE_ID);

        restSalonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalon.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalon))
            )
            .andExpect(status().isOk());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);
        Salon testSalon = salonList.get(salonList.size() - 1);
        assertThat(testSalon.getSalonId()).isEqualTo(UPDATED_SALON_ID);
        assertThat(testSalon.getSectieId()).isEqualTo(UPDATED_SECTIE_ID);
    }

    @Test
    @Transactional
    void patchNonExistingSalon() throws Exception {
        int databaseSizeBeforeUpdate = salonRepository.findAll().size();
        salon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salon.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalon() throws Exception {
        int databaseSizeBeforeUpdate = salonRepository.findAll().size();
        salon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalon() throws Exception {
        int databaseSizeBeforeUpdate = salonRepository.findAll().size();
        salon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalonMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salon))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Salon in the database
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon);
    }

    @Test
    @Transactional
    void deleteSalon() throws Exception {
        // Initialize the database
        salonRepository.saveAndFlush(salon);

        int databaseSizeBeforeDelete = salonRepository.findAll().size();

        // Delete the salon
        restSalonMockMvc
            .perform(delete(ENTITY_API_URL_ID, salon.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Salon> salonList = salonRepository.findAll();
        assertThat(salonList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(1)).deleteById(salon.getId());
    }

    @Test
    @Transactional
    void searchSalon() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        salonRepository.saveAndFlush(salon);
        when(mockSalonSearchRepository.search(queryStringQuery("id:" + salon.getId()))).thenReturn(Collections.singletonList(salon));

        // Search the salon
        restSalonMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + salon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salon.getId().intValue())))
            .andExpect(jsonPath("$.[*].salonId").value(hasItem(DEFAULT_SALON_ID.intValue())))
            .andExpect(jsonPath("$.[*].sectieId").value(hasItem(DEFAULT_SECTIE_ID.intValue())));
    }
}
