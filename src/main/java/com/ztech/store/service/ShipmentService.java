package com.ztech.store.service;

import com.ztech.store.domain.Shipment;
import com.ztech.store.repository.ShipmentRepository;
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
 * Service Implementation for managing {@link Shipment}.
 */
@Service
@Transactional
public class ShipmentService {

    private final Logger log = LoggerFactory.getLogger(ShipmentService.class);

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    /**
     * Save a shipment.
     *
     * @param shipment the entity to save.
     * @return the persisted entity.
     */
    public Mono<Shipment> save(Shipment shipment) {
        log.debug("Request to save Shipment : {}", shipment);
        return shipmentRepository.save(shipment);
    }

    /**
     * Partially update a shipment.
     *
     * @param shipment the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Shipment> partialUpdate(Shipment shipment) {
        log.debug("Request to partially update Shipment : {}", shipment);

        return shipmentRepository
            .findById(shipment.getId())
            .map(existingShipment -> {
                if (shipment.getTrackingCode() != null) {
                    existingShipment.setTrackingCode(shipment.getTrackingCode());
                }
                if (shipment.getDate() != null) {
                    existingShipment.setDate(shipment.getDate());
                }
                if (shipment.getDetails() != null) {
                    existingShipment.setDetails(shipment.getDetails());
                }

                return existingShipment;
            })
            .flatMap(shipmentRepository::save);
    }

    /**
     * Get all the shipments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Shipment> findAll(Pageable pageable) {
        log.debug("Request to get all Shipments");

        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        .flatMapMany(result -> {
            if(result){
                return shipmentRepository.findAllBy(pageable); 
            } else {
                return SecurityUtils.getCurrentUserLogin()
                .flatMapMany(currentUserLogin -> {
                    return shipmentRepository
                    .findAllByInvoiceOrderCustomerUserLogin(currentUserLogin, pageable);
                });
            }
        });
    }

    /**
     * Returns the number of shipments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return shipmentRepository.count();
    }

    /**
     * Get one shipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Shipment> findOne(Long id) {
        log.debug("Request to get Shipment : {}", id);
                
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
        .flatMap(result -> {
            if(result){
                return shipmentRepository.findById(id); 
            } else {
                return SecurityUtils.getCurrentUserLogin()
                .flatMap(currentUserLogin -> {
                    return shipmentRepository
                    .findOneByIdAndCustomerUserLogin(id, currentUserLogin);
                });
            }
        });
    }

    /**
     * Delete the shipment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Shipment : {}", id);
        return shipmentRepository.deleteById(id);
    }
}
