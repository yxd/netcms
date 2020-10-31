package com.myapp.web.rest;

import com.myapp.MyApp;
import com.myapp.domain.D;
import com.myapp.repository.DRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link DResource} REST controller.
 */
@SpringBootTest(classes = MyApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class DResourceIT {

    @Autowired
    private DRepository dRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDMockMvc;

    private D d;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static D createEntity(EntityManager em) {
        D d = new D();
        return d;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static D createUpdatedEntity(EntityManager em) {
        D d = new D();
        return d;
    }

    @BeforeEach
    public void initTest() {
        d = createEntity(em);
    }

    @Test
    @Transactional
    public void createD() throws Exception {
        int databaseSizeBeforeCreate = dRepository.findAll().size();
        // Create the D
        restDMockMvc.perform(post("/api/ds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(d)))
            .andExpect(status().isCreated());

        // Validate the D in the database
        List<D> dList = dRepository.findAll();
        assertThat(dList).hasSize(databaseSizeBeforeCreate + 1);
        D testD = dList.get(dList.size() - 1);
    }

    @Test
    @Transactional
    public void createDWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dRepository.findAll().size();

        // Create the D with an existing ID
        d.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDMockMvc.perform(post("/api/ds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(d)))
            .andExpect(status().isBadRequest());

        // Validate the D in the database
        List<D> dList = dRepository.findAll();
        assertThat(dList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllDS() throws Exception {
        // Initialize the database
        dRepository.saveAndFlush(d);

        // Get all the dList
        restDMockMvc.perform(get("/api/ds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(d.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getD() throws Exception {
        // Initialize the database
        dRepository.saveAndFlush(d);

        // Get the d
        restDMockMvc.perform(get("/api/ds/{id}", d.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(d.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingD() throws Exception {
        // Get the d
        restDMockMvc.perform(get("/api/ds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateD() throws Exception {
        // Initialize the database
        dRepository.saveAndFlush(d);

        int databaseSizeBeforeUpdate = dRepository.findAll().size();

        // Update the d
        D updatedD = dRepository.findById(d.getId()).get();
        // Disconnect from session so that the updates on updatedD are not directly saved in db
        em.detach(updatedD);

        restDMockMvc.perform(put("/api/ds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedD)))
            .andExpect(status().isOk());

        // Validate the D in the database
        List<D> dList = dRepository.findAll();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
        D testD = dList.get(dList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDMockMvc.perform(put("/api/ds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(d)))
            .andExpect(status().isBadRequest());

        // Validate the D in the database
        List<D> dList = dRepository.findAll();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteD() throws Exception {
        // Initialize the database
        dRepository.saveAndFlush(d);

        int databaseSizeBeforeDelete = dRepository.findAll().size();

        // Delete the d
        restDMockMvc.perform(delete("/api/ds/{id}", d.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<D> dList = dRepository.findAll();
        assertThat(dList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
