package com.ztech.store.service;

import com.ztech.store.domain.OrderedItem;
import com.ztech.store.repository.OrderedItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link OrderedItem}.
 */
@Service
@Transactional
public class OrderedItemService {

    private final Logger log = LoggerFactory.getLogger(OrderedItemService.class);

    private final OrderedItemRepository orderedItemRepository;

    public OrderedItemService(OrderedItemRepository orderedItemRepository) {
        this.orderedItemRepository = orderedItemRepository;
    }

    /**
     * Save a orderedItem.
     *
     * @param orderedItem the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderedItem> save(OrderedItem orderedItem) {
        log.debug("Request to save OrderedItem : {}", orderedItem);
        return orderedItemRepository.save(orderedItem);
    }

    /**
     * Partially update a orderedItem.
     *
     * @param orderedItem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderedItem> partialUpdate(OrderedItem orderedItem) {
        log.debug("Request to partially update OrderedItem : {}", orderedItem);

        return orderedItemRepository
            .findById(orderedItem.getId())
            .map(existingOrderedItem -> {
                if (orderedItem.getQuantity() != null) {
                    existingOrderedItem.setQuantity(orderedItem.getQuantity());
                }
                if (orderedItem.getTotalPrice() != null) {
                    existingOrderedItem.setTotalPrice(orderedItem.getTotalPrice());
                }
                if (orderedItem.getStatus() != null) {
                    existingOrderedItem.setStatus(orderedItem.getStatus());
                }

                return existingOrderedItem;
            })
            .flatMap(orderedItemRepository::save);
    }

    /**
     * Get all the orderedItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderedItem> findAll(Pageable pageable) {
        log.debug("Request to get all OrderedItems");
        return orderedItemRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of orderedItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderedItemRepository.count();
    }

    /**
     * Get one orderedItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderedItem> findOne(Long id) {
        log.debug("Request to get OrderedItem : {}", id);
        return orderedItemRepository.findById(id);
    }

    /**
     * Delete the orderedItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete OrderedItem : {}", id);
        return orderedItemRepository.deleteById(id);
    }
}
