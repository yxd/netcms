package com.myapp.web.rest;

import com.myapp.domain.B;
import com.myapp.repository.BRepository;
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
 * REST controller for managing {@link com.myapp.domain.B}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BResource {

    private final Logger log = LoggerFactory.getLogger(BResource.class);

    private static final String ENTITY_NAME = "b";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BRepository bRepository;

    public BResource(BRepository bRepository) {
        this.bRepository = bRepository;
    }

    /**
     * {@code POST  /bs} : Create a new b.
     *
     * @param b the b to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new b, or with status {@code 400 (Bad Request)} if the b has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bs")
    public ResponseEntity<B> createB(@RequestBody B b) throws URISyntaxException {
        log.debug("REST request to save B : {}", b);
        if (b.getId() != null) {
            throw new BadRequestAlertException("A new b cannot already have an ID", ENTITY_NAME, "idexists");
        }
        B result = bRepository.save(b);
        return ResponseEntity.created(new URI("/api/bs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bs} : Updates an existing b.
     *
     * @param b the b to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated b,
     * or with status {@code 400 (Bad Request)} if the b is not valid,
     * or with status {@code 500 (Internal Server Error)} if the b couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bs")
    public ResponseEntity<B> updateB(@RequestBody B b) throws URISyntaxException {
        log.debug("REST request to update B : {}", b);
        if (b.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        B result = bRepository.save(b);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, b.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /bs} : get all the bs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bs in body.
     */
    @GetMapping("/bs")
    public List<B> getAllBS() {
        log.debug("REST request to get all BS");
        return bRepository.findAll();
    }

    /**
     * {@code GET  /bs/:id} : get the "id" b.
     *
     * @param id the id of the b to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the b, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bs/{id}")
    public ResponseEntity<B> getB(@PathVariable Long id) {
        log.debug("REST request to get B : {}", id);
        Optional<B> b = bRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(b);
    }

    /**
     * {@code DELETE  /bs/:id} : delete the "id" b.
     *
     * @param id the id of the b to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bs/{id}")
    public ResponseEntity<Void> deleteB(@PathVariable Long id) {
        log.debug("REST request to delete B : {}", id);
        bRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
