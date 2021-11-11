package com.ztech.store.service;

import com.ztech.store.domain.ProductRating;
import com.ztech.store.repository.ProductRatingRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ProductRating}.
 */
@Service
@Transactional
public class ProductRatingService {

    private final Logger log = LoggerFactory.getLogger(ProductRatingService.class);

    private final ProductRatingRepository productRatingRepository;

    public ProductRatingService(ProductRatingRepository productRatingRepository) {
        this.productRatingRepository = productRatingRepository;
    }

    /**
     * Save a productRating.
     *
     * @param productRating the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductRating> save(ProductRating productRating) {
        log.debug("Request to save ProductRating : {}", productRating);
        return productRatingRepository.save(productRating);
    }

    /**
     * Partially update a productRating.
     *
     * @param productRating the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductRating> partialUpdate(ProductRating productRating) {
        log.debug("Request to partially update ProductRating : {}", productRating);

        return productRatingRepository
            .findById(productRating.getId())
            .map(existingProductRating -> {
                if (productRating.getRating() != null) {
                    existingProductRating.setRating(productRating.getRating());
                }
                if (productRating.getDate() != null) {
                    existingProductRating.setDate(productRating.getDate());
                }

                return existingProductRating;
            })
            .flatMap(productRatingRepository::save);
    }

    /**
     * Get all the productRatings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductRating> findAll() {
        log.debug("Request to get all ProductRatings");
        return productRatingRepository.findAll();
    }

    /**
     * Returns the number of productRatings available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productRatingRepository.count();
    }

    /**
     * Get one productRating by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductRating> findOne(Long id) {
        log.debug("Request to get ProductRating : {}", id);
        return productRatingRepository.findById(id);
    }

    /**
     * Delete the productRating by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductRating : {}", id);
        return productRatingRepository.deleteById(id);
    }
}
