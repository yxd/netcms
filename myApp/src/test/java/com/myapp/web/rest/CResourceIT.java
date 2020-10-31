package com.myapp.web.rest;

import com.myapp.MyApp;
import com.myapp.domain.C;
import com.myapp.repository.CRepository;

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
 * Integration tests for the {@link CResource} REST controller.
 */
@SpringBootTest(classes = MyApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class CResourceIT {

    @Autowired
    private CRepository cRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCMockMvc;

    private C c;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static C createEntity(EntityManager em) {
        C c = new C();
        return c;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static C createUpdatedEntity(EntityManager em) {
        C c = new C();
        return c;
    }

    @BeforeEach
    public void initTest() {
        c = createEntity(em);
    }

    @Test
    @Transactional
    public void createC() throws Exception {
        int databaseSizeBeforeCreate = cRepository.findAll().size();
        // Create the C
        restCMockMvc.perform(post("/api/cs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isCreated());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeCreate + 1);
        C testC = cList.get(cList.size() - 1);
    }

    @Test
    @Transactional
    public void createCWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cRepository.findAll().size();

        // Create the C with an existing ID
        c.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCMockMvc.perform(post("/api/cs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllCS() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        // Get all the cList
        restCMockMvc.perform(get("/api/cs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(c.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getC() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        // Get the c
        restCMockMvc.perform(get("/api/cs/{id}", c.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(c.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingC() throws Exception {
        // Get the c
        restCMockMvc.perform(get("/api/cs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateC() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        int databaseSizeBeforeUpdate = cRepository.findAll().size();

        // Update the c
        C updatedC = cRepository.findById(c.getId()).get();
        // Disconnect from session so that the updates on updatedC are not directly saved in db
        em.detach(updatedC);

        restCMockMvc.perform(put("/api/cs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedC)))
            .andExpect(status().isOk());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
        C testC = cList.get(cList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCMockMvc.perform(put("/api/cs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteC() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        int databaseSizeBeforeDelete = cRepository.findAll().size();

        // Delete the c
        restCMockMvc.perform(delete("/api/cs/{id}", c.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
