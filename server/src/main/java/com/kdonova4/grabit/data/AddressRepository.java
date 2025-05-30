package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findAddressByUser(AppUser user);
}
