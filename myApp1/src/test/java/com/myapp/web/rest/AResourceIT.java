package com.myapp.web.rest;

import com.myapp.MyApp1App;
import com.myapp.domain.A;
import com.myapp.repository.ARepository;

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
 * Integration tests for the {@link AResource} REST controller.
 */
@SpringBootTest(classes = MyApp1App.class)
@AutoConfigureMockMvc
@WithMockUser
public class AResourceIT {

    @Autowired
    private ARepository aRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAMockMvc;

    private A a;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static A createEntity(EntityManager em) {
        A a = new A();
        return a;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static A createUpdatedEntity(EntityManager em) {
        A a = new A();
        return a;
    }

    @BeforeEach
    public void initTest() {
        a = createEntity(em);
    }

    @Test
    @Transactional
    public void createA() throws Exception {
        int databaseSizeBeforeCreate = aRepository.findAll().size();
        // Create the A
        restAMockMvc.perform(post("/api/as")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(a)))
            .andExpect(status().isCreated());

        // Validate the A in the database
        List<A> aList = aRepository.findAll();
        assertThat(aList).hasSize(databaseSizeBeforeCreate + 1);
        A testA = aList.get(aList.size() - 1);
    }

    @Test
    @Transactional
    public void createAWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = aRepository.findAll().size();

        // Create the A with an existing ID
        a.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAMockMvc.perform(post("/api/as")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(a)))
            .andExpect(status().isBadRequest());

        // Validate the A in the database
        List<A> aList = aRepository.findAll();
        assertThat(aList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllAS() throws Exception {
        // Initialize the database
        aRepository.saveAndFlush(a);

        // Get all the aList
        restAMockMvc.perform(get("/api/as?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(a.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getA() throws Exception {
        // Initialize the database
        aRepository.saveAndFlush(a);

        // Get the a
        restAMockMvc.perform(get("/api/as/{id}", a.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(a.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingA() throws Exception {
        // Get the a
        restAMockMvc.perform(get("/api/as/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateA() throws Exception {
        // Initialize the database
        aRepository.saveAndFlush(a);

        int databaseSizeBeforeUpdate = aRepository.findAll().size();

        // Update the a
        A updatedA = aRepository.findById(a.getId()).get();
        // Disconnect from session so that the updates on updatedA are not directly saved in db
        em.detach(updatedA);

        restAMockMvc.perform(put("/api/as")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedA)))
            .andExpect(status().isOk());

        // Validate the A in the database
        List<A> aList = aRepository.findAll();
        assertThat(aList).hasSize(databaseSizeBeforeUpdate);
        A testA = aList.get(aList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingA() throws Exception {
        int databaseSizeBeforeUpdate = aRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAMockMvc.perform(put("/api/as")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(a)))
            .andExpect(status().isBadRequest());

        // Validate the A in the database
        List<A> aList = aRepository.findAll();
        assertThat(aList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteA() throws Exception {
        // Initialize the database
        aRepository.saveAndFlush(a);

        int databaseSizeBeforeDelete = aRepository.findAll().size();

        // Delete the a
        restAMockMvc.perform(delete("/api/as/{id}", a.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<A> aList = aRepository.findAll();
        assertThat(aList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
