package com.ztech.store.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;


import com.ztech.store.domain.ProductOrder;
import com.ztech.store.repository.ProductOrderRepository;
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
 * Service Implementation for managing {@link ProductOrder}.
 */
@Service
@Transactional
public class ProductOrderService {

    private final Logger log = LoggerFactory.getLogger(ProductOrderService.class);

    private final ProductOrderRepository productOrderRepository;

    public ProductOrderService(ProductOrderRepository productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
    }

    /**
     * Save a productOrder.
     *
     * @param productOrder the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductOrder> save(ProductOrder productOrder) {
        log.debug("Request to save ProductOrder : {}", productOrder);
        return productOrderRepository.save(productOrder);
    }

    /**
     * Partially update a productOrder.
     *
     * @param productOrder the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductOrder> partialUpdate(ProductOrder productOrder) {
        log.debug("Request to partially update ProductOrder : {}", productOrder);

        return productOrderRepository
            .findById(productOrder.getId())
            .map(existingProductOrder -> {
                if (productOrder.getPlacedDate() != null) {
                    existingProductOrder.setPlacedDate(productOrder.getPlacedDate());
                }
                if (productOrder.getStatus() != null) {
                    existingProductOrder.setStatus(productOrder.getStatus());
                }
                if (productOrder.getCode() != null) {
                    existingProductOrder.setCode(productOrder.getCode());
                }

                return existingProductOrder;
            })
            .flatMap(productOrderRepository::save);
    }

    /**
     * Get all the productOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductOrder> findAll(Pageable pageable) {
        log.debug("Request to get all ProductOrders");
        
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
                        .flatMapMany(result -> {
                            if(result){
                                return productOrderRepository.findAllBy(pageable); 
                            } else {
                                return SecurityUtils.getCurrentUserLogin()
                                .flatMapMany(currentUserLogin -> {
                                    return productOrderRepository
                                    .findAllByCustomerUserLogin(currentUserLogin, pageable);
                                });
                            }
                        });
                
    }

    /**
     * Returns the number of productOrders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productOrderRepository.count();
    }

    /**
     * Get one productOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductOrder> findOne(Long id) {
        log.debug("Request to get ProductOrder : {}", id);

        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
                .flatMap(result -> {
                    if(result){
                        return productOrderRepository.findById(id); 
                    } else {
                        return SecurityUtils.getCurrentUserLogin()
                        .flatMap(currentUserLogin -> {
                            return productOrderRepository
                            .findOneByIdAndCustomerUserLogin(id, currentUserLogin);
                        });
                    }
                });
    }

    /**
     * Delete the productOrder by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ProductOrder : {}", id);
        return productOrderRepository.deleteById(id);
    }
}
