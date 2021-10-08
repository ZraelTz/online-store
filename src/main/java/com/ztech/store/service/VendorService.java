package com.ztech.store.service;

import com.ztech.store.domain.Vendor;
import com.ztech.store.repository.VendorRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Vendor}.
 */
@Service
@Transactional
public class VendorService {

    private final Logger log = LoggerFactory.getLogger(VendorService.class);

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Save a vendor.
     *
     * @param vendor the entity to save.
     * @return the persisted entity.
     */
    public Mono<Vendor> save(Vendor vendor) {
        log.debug("Request to save Vendor : {}", vendor);
        return vendorRepository.save(vendor);
    }

    /**
     * Partially update a vendor.
     *
     * @param vendor the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Vendor> partialUpdate(Vendor vendor) {
        log.debug("Request to partially update Vendor : {}", vendor);

        return vendorRepository
            .findById(vendor.getId())
            .map(existingVendor -> {
                if (vendor.getFirstName() != null) {
                    existingVendor.setFirstName(vendor.getFirstName());
                }
                if (vendor.getLastName() != null) {
                    existingVendor.setLastName(vendor.getLastName());
                }
                if (vendor.getGender() != null) {
                    existingVendor.setGender(vendor.getGender());
                }
                if (vendor.getEmail() != null) {
                    existingVendor.setEmail(vendor.getEmail());
                }
                if (vendor.getPhone() != null) {
                    existingVendor.setPhone(vendor.getPhone());
                }
                if (vendor.getPhone2() != null) {
                    existingVendor.setPhone2(vendor.getPhone2());
                }
                if (vendor.getAddressLine1() != null) {
                    existingVendor.setAddressLine1(vendor.getAddressLine1());
                }
                if (vendor.getAddressLine2() != null) {
                    existingVendor.setAddressLine2(vendor.getAddressLine2());
                }
                if (vendor.getCity() != null) {
                    existingVendor.setCity(vendor.getCity());
                }
                if (vendor.getCountry() != null) {
                    existingVendor.setCountry(vendor.getCountry());
                }
                if (vendor.getStoreName() != null) {
                    existingVendor.setStoreName(vendor.getStoreName());
                }
                if (vendor.getBusinessAccountNumber() != null) {
                    existingVendor.setBusinessAccountNumber(vendor.getBusinessAccountNumber());
                }

                return existingVendor;
            })
            .flatMap(vendorRepository::save);
    }

    /**
     * Get all the vendors.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Vendor> findAll() {
        log.debug("Request to get all Vendors");
        return vendorRepository.findAll();
    }

    /**
     * Returns the number of vendors available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return vendorRepository.count();
    }

    /**
     * Get one vendor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Vendor> findOne(Long id) {
        log.debug("Request to get Vendor : {}", id);
        return vendorRepository.findById(id);
    }

    /**
     * Delete the vendor by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Vendor : {}", id);
        return vendorRepository.deleteById(id);
    }
}
