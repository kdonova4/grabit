package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.BidRepository;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Bid;
import com.kdonova4.grabit.model.Product;

import java.util.List;
import java.util.Optional;

public class BidService {
    private final BidRepository repository;

    public BidService(BidRepository repository) {
        this.repository = repository;
    }

    public List<Bid> findAll() {
        return repository.findAll();
    }

    public List<Bid> findByUser(AppUser user) {
        return repository.findByUser(user);
    }

    public List<Bid> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public Optional<Bid> findById(int id) {
        return repository.findById(id);
    }
}
