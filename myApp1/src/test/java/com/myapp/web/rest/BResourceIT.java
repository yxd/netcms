package com.myapp.web.rest;

import com.myapp.MyApp1App;
import com.myapp.domain.B;
import com.myapp.repository.BRepository;

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
 * Integration tests for the {@link BResource} REST controller.
 */
@SpringBootTest(classes = MyApp1App.class)
@AutoConfigureMockMvc
@WithMockUser
public class BResourceIT {

    @Autowired
    private BRepository bRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBMockMvc;

    private B b;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static B createEntity(EntityManager em) {
        B b = new B();
        return b;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static B createUpdatedEntity(EntityManager em) {
        B b = new B();
        return b;
    }

    @BeforeEach
    public void initTest() {
        b = createEntity(em);
    }

    @Test
    @Transactional
    public void createB() throws Exception {
        int databaseSizeBeforeCreate = bRepository.findAll().size();
        // Create the B
        restBMockMvc.perform(post("/api/bs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isCreated());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeCreate + 1);
        B testB = bList.get(bList.size() - 1);
    }

    @Test
    @Transactional
    public void createBWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bRepository.findAll().size();

        // Create the B with an existing ID
        b.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBMockMvc.perform(post("/api/bs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllBS() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        // Get all the bList
        restBMockMvc.perform(get("/api/bs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(b.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getB() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        // Get the b
        restBMockMvc.perform(get("/api/bs/{id}", b.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(b.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingB() throws Exception {
        // Get the b
        restBMockMvc.perform(get("/api/bs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateB() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeUpdate = bRepository.findAll().size();

        // Update the b
        B updatedB = bRepository.findById(b.getId()).get();
        // Disconnect from session so that the updates on updatedB are not directly saved in db
        em.detach(updatedB);

        restBMockMvc.perform(put("/api/bs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedB)))
            .andExpect(status().isOk());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBMockMvc.perform(put("/api/bs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(b)))
            .andExpect(status().isBadRequest());

        // Validate the B in the database
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteB() throws Exception {
        // Initialize the database
        bRepository.saveAndFlush(b);

        int databaseSizeBeforeDelete = bRepository.findAll().size();

        // Delete the b
        restBMockMvc.perform(delete("/api/bs/{id}", b.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<B> bList = bRepository.findAll();
        assertThat(bList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
