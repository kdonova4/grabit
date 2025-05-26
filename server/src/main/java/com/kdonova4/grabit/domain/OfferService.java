package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.OfferRepository;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Offer;
import com.kdonova4.grabit.model.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OfferService {
    private final OfferRepository repository;

    public OfferService(OfferRepository repository) {
        this.repository = repository;
    }

    public List<Offer> findAll() {
        return repository.findAll();
    }

    public List<Offer> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public List<Offer> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public Optional<Offer> findByUserAndProduct(AppUser user, Product product) {
        return repository.findByUserAndProduct(user, product);
    }

    public List<Offer> findByProductAndExpireDateAfter(Product product, LocalDateTime now) {
        return repository.findByProductAndExpireDateAfter(product, now);
    }

    public Optional<Offer> findById(int id) {
        return repository.findById(id);
    }
}
