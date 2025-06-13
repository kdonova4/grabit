package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.AddressService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.model.Address;
import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.security.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Address Controller", description = "Address Operations")
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService service;
    private final AppUserService appUserService;

    public AddressController(AddressService service, AppUserService appUserService) {
        this.service = service;
        this.appUserService = appUserService;
    }

    @GetMapping()
    @Operation(summary = "Finds All Addresses")
    public ResponseEntity<List<Address>> findAll() {
        List<Address> addresses = service.findAll();

        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/user/{appUserId}")
    @Operation(summary = "Finds The Address Associated With A User")
    public ResponseEntity<List<Address>> findByUser(@PathVariable int appUserId) {
        Optional<AppUser> appUser = appUserService.findUserById(appUserId);

        if(appUser.isPresent()) {
            List<Address> addresses = service.findByUser(appUser.get());

            return ResponseEntity.ok(addresses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Find An Address By Its ID")
    public ResponseEntity<Address> findById(@PathVariable int addressId) {
        Optional<Address> address = service.findById(addressId);

        if(address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(address.get());
    }

    @PostMapping
    @Operation(summary = "Add An Address")
    public ResponseEntity<Object> create(@RequestBody Address address) {
        Result<Address> result = service.create(address);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Updates An Address")
    public ResponseEntity<Object> update(@PathVariable int addressId, @RequestBody Address address) {
        if(addressId != address.getAddressId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Address> result = service.update(address);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
