package com.ztech.store.service;

import com.ztech.store.domain.Checkout;
import com.ztech.store.repository.CheckoutRepository;
import com.ztech.store.security.AuthoritiesConstants;
import com.ztech.store.security.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Checkout}.
 */
@Service
@Transactional
public class CheckoutService {

    private final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CheckoutRepository checkoutRepository;

    public CheckoutService(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    /**
     * Save a checkout.
     *
     * @param checkout the entity to save.
     * @return the persisted entity.
     */
    public Mono<Checkout> save(Checkout checkout) {
        log.debug("Request to save Checkout : {}", checkout);
        return checkoutRepository.save(checkout);
    }

    /**
     * Partially update a checkout.
     *
     * @param checkout the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Checkout> partialUpdate(Checkout checkout) {
        log.debug("Request to partially update Checkout : {}", checkout);

        return checkoutRepository
            .findById(checkout.getId())
            .map(existingCheckout -> {
                if (checkout.getQuantity() != null) {
                    existingCheckout.setQuantity(checkout.getQuantity());
                }
                if (checkout.getTotalPrice() != null) {
                    existingCheckout.setTotalPrice(checkout.getTotalPrice());
                }

                return existingCheckout;
            })
            .flatMap(checkoutRepository::save);
    }

    /**
     * Get all the checkouts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Checkout> findAll() {
        log.debug("Request to get all Checkouts");
        
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        .flatMapMany(result -> {
            if(result){
                return checkoutRepository.findAll(); 
            } else {
                return SecurityUtils.getCurrentUserLogin()
                .flatMapMany(currentUserLogin -> {
                    return checkoutRepository
                    .findAllByCustomerUserLogin(currentUserLogin);
                });
            }
        });
    }

    /**
     *  Get all the checkouts where Cart is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Checkout> findAllWhereCartIsNull() {
        log.debug("Request to get all checkouts where Cart is null");
        return checkoutRepository.findAllWhereCartIsNull();
    }

    /**
     * Returns the number of checkouts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return checkoutRepository.count();
    }

    /**
     * Get one checkout by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Checkout> findOne(Long id) {
        log.debug("Request to get Checkout : {}", id);
        return checkoutRepository.findById(id);
    }

    /**
     * Delete the checkout by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Checkout : {}", id);
        return checkoutRepository.deleteById(id);
    }
}
