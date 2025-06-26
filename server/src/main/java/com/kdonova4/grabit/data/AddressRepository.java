package com.kdonova4.grabit.data;

import com.kdonova4.grabit.model.entity.Address;
import com.kdonova4.grabit.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findAddressByUser(AppUser user);
}
