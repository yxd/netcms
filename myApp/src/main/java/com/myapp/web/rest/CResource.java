package com.myapp.web.rest;

import com.myapp.domain.C;
import com.myapp.repository.CRepository;
import com.myapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.myapp.domain.C}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CResource {

    private final Logger log = LoggerFactory.getLogger(CResource.class);

    private static final String ENTITY_NAME = "c";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CRepository cRepository;

    public CResource(CRepository cRepository) {
        this.cRepository = cRepository;
    }

    /**
     * {@code POST  /cs} : Create a new c.
     *
     * @param c the c to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new c, or with status {@code 400 (Bad Request)} if the c has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cs")
    public ResponseEntity<C> createC(@RequestBody C c) throws URISyntaxException {
        log.debug("REST request to save C : {}", c);
        if (c.getId() != null) {
            throw new BadRequestAlertException("A new c cannot already have an ID", ENTITY_NAME, "idexists");
        }
        C result = cRepository.save(c);
        return ResponseEntity.created(new URI("/api/cs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cs} : Updates an existing c.
     *
     * @param c the c to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated c,
     * or with status {@code 400 (Bad Request)} if the c is not valid,
     * or with status {@code 500 (Internal Server Error)} if the c couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cs")
    public ResponseEntity<C> updateC(@RequestBody C c) throws URISyntaxException {
        log.debug("REST request to update C : {}", c);
        if (c.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        C result = cRepository.save(c);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, c.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /cs} : get all the cs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cs in body.
     */
    @GetMapping("/cs")
    public List<C> getAllCS() {
        log.debug("REST request to get all CS");
        return cRepository.findAll();
    }

    /**
     * {@code GET  /cs/:id} : get the "id" c.
     *
     * @param id the id of the c to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the c, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cs/{id}")
    public ResponseEntity<C> getC(@PathVariable Long id) {
        log.debug("REST request to get C : {}", id);
        Optional<C> c = cRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(c);
    }

    /**
     * {@code DELETE  /cs/:id} : delete the "id" c.
     *
     * @param id the id of the c to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cs/{id}")
    public ResponseEntity<Void> deleteC(@PathVariable Long id) {
        log.debug("REST request to delete C : {}", id);
        cRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
