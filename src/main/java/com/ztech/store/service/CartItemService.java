package com.ztech.store.service;

import com.ztech.store.domain.CartItem;
import com.ztech.store.repository.CartItemRepository;
import com.ztech.store.security.AuthoritiesConstants;
import com.ztech.store.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CartItem}.
 */
@Service
@Transactional
public class CartItemService {

    private final Logger log = LoggerFactory.getLogger(CartItemService.class);

    private final CartItemRepository cartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Save a cartItem.
     *
     * @param cartItem the entity to save.
     * @return the persisted entity.
     */
    public Mono<CartItem> save(CartItem cartItem) {
        log.debug("Request to save CartItem : {}", cartItem);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Partially update a cartItem.
     *
     * @param cartItem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CartItem> partialUpdate(CartItem cartItem) {
        log.debug("Request to partially update CartItem : {}", cartItem);

        return cartItemRepository
            .findById(cartItem.getId())
            .map(existingCartItem -> {
                if (cartItem.getQuantity() != null) {
                    existingCartItem.setQuantity(cartItem.getQuantity());
                }

                return existingCartItem;
            })
            .flatMap(cartItemRepository::save);
    }

    /**
     * Get all the cartItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */

    @Transactional(readOnly = true)
    public Flux<CartItem> findAll(Pageable pageable) {
        log.debug("Request to get all CartItems");
        
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        .flatMapMany(result -> {
            if(result){
                return cartItemRepository.findAllBy(pageable); 
            } else {
                return SecurityUtils.getCurrentUserLogin()
                .flatMapMany(currentUserLogin -> {
                    return cartItemRepository
                    .findAllByCustomerUserLogin(currentUserLogin, pageable);
                });
            }
        });
    }

    /**
     * Returns the number of cartItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cartItemRepository.count();
    }

    /**
     * Get one cartItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CartItem> findOne(Long id) {
        log.debug("Request to get CartItem : {}", id);

        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        .flatMap(result -> {
            if(result){
                return cartItemRepository.findById(id); 
            } else {
                return SecurityUtils.getCurrentUserLogin()
                .flatMap(currentUserLogin -> {
                    return cartItemRepository
                    .findOneByIdAndCustomerUserLogin(id, currentUserLogin);
                });
            }
        });
    }

    /**
     * Delete the cartItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete CartItem : {}", id);
        return cartItemRepository.deleteById(id);
    }
}
