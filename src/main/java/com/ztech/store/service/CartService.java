package com.ztech.store.service;

import com.ztech.store.domain.Cart;
import com.ztech.store.repository.CartRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Cart}.
 */
@Service
@Transactional
public class CartService {

    private final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Save a cart.
     *
     * @param cart the entity to save.
     * @return the persisted entity.
     */
    public Mono<Cart> save(Cart cart) {
        log.debug("Request to save Cart : {}", cart);
        return cartRepository.save(cart);
    }

    /**
     * Partially update a cart.
     *
     * @param cart the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Cart> partialUpdate(Cart cart) {
        log.debug("Request to partially update Cart : {}", cart);

        return cartRepository
            .findById(cart.getId())
            .map(existingCart -> {
                if (cart.getDate() != null) {
                    existingCart.setDate(cart.getDate());
                }

                return existingCart;
            })
            .flatMap(cartRepository::save);
    }

    /**
     * Get all the carts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Cart> findAll() {
        log.debug("Request to get all Carts");
        return cartRepository.findAll();
    }

    /**
     * Returns the number of carts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cartRepository.count();
    }

    /**
     * Get one cart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Cart> findOne(Long id) {
        log.debug("Request to get Cart : {}", id);
        return cartRepository.findById(id);
    }

    /**
     * Delete the cart by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        return cartRepository.deleteById(id);
    }
}
