package com.myapp.web.rest;

import com.myapp.domain.A;
import com.myapp.repository.ARepository;
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
 * REST controller for managing {@link com.myapp.domain.A}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AResource {

    private final Logger log = LoggerFactory.getLogger(AResource.class);

    private static final String ENTITY_NAME = "myApp1A";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ARepository aRepository;

    public AResource(ARepository aRepository) {
        this.aRepository = aRepository;
    }

    /**
     * {@code POST  /as} : Create a new a.
     *
     * @param a the a to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new a, or with status {@code 400 (Bad Request)} if the a has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/as")
    public ResponseEntity<A> createA(@RequestBody A a) throws URISyntaxException {
        log.debug("REST request to save A : {}", a);
        if (a.getId() != null) {
            throw new BadRequestAlertException("A new a cannot already have an ID", ENTITY_NAME, "idexists");
        }
        A result = aRepository.save(a);
        return ResponseEntity.created(new URI("/api/as/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /as} : Updates an existing a.
     *
     * @param a the a to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated a,
     * or with status {@code 400 (Bad Request)} if the a is not valid,
     * or with status {@code 500 (Internal Server Error)} if the a couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/as")
    public ResponseEntity<A> updateA(@RequestBody A a) throws URISyntaxException {
        log.debug("REST request to update A : {}", a);
        if (a.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        A result = aRepository.save(a);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, a.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /as} : get all the as.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of as in body.
     */
    @GetMapping("/as")
    public List<A> getAllAS() {
        log.debug("REST request to get all AS");
        return aRepository.findAll();
    }

    /**
     * {@code GET  /as/:id} : get the "id" a.
     *
     * @param id the id of the a to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the a, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/as/{id}")
    public ResponseEntity<A> getA(@PathVariable Long id) {
        log.debug("REST request to get A : {}", id);
        Optional<A> a = aRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(a);
    }

    /**
     * {@code DELETE  /as/:id} : delete the "id" a.
     *
     * @param id the id of the a to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/as/{id}")
    public ResponseEntity<Void> deleteA(@PathVariable Long id) {
        log.debug("REST request to delete A : {}", id);
        aRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
