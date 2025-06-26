package com.kdonova4.grabit.controller;

import com.kdonova4.grabit.domain.AddressService;
import com.kdonova4.grabit.domain.Result;
import com.kdonova4.grabit.domain.mapper.AddressMapper;
import com.kdonova4.grabit.model.dto.AddressCreateDTO;
import com.kdonova4.grabit.model.dto.AddressResponseDTO;
import com.kdonova4.grabit.model.dto.AddressUpdateDTO;
import com.kdonova4.grabit.model.entity.*;
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
    public ResponseEntity<List<AddressResponseDTO>> findAll() {
        List<Address> addresses = service.findAll();
        List<AddressResponseDTO> addressResponseDTOS = addresses.stream().map(AddressMapper::toResponseDTO).toList();
        return ResponseEntity.ok(addressResponseDTOS);
    }

    @GetMapping("/user/{appUserId}")
    @Operation(summary = "Finds The Address Associated With A User")
    public ResponseEntity<List<AddressResponseDTO>> findByUser(@PathVariable int appUserId) {
        Optional<AppUser> appUser = appUserService.findUserById(appUserId);

        if(appUser.isPresent()) {
            List<Address> addresses = service.findByUser(appUser.get());

            List<AddressResponseDTO> addressResponseDTOS = addresses.stream().map(AddressMapper::toResponseDTO).toList();

            return ResponseEntity.ok(addressResponseDTOS);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Find An Address By Its ID")
    public ResponseEntity<AddressResponseDTO> findById(@PathVariable int addressId) {
        Optional<Address> address = service.findById(addressId);

        if(address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AddressMapper.toResponseDTO(address.get()));
    }

    @PostMapping
    @Operation(summary = "Add An Address")
    public ResponseEntity<Object> create(@RequestBody AddressCreateDTO address) {
        Result<AddressResponseDTO> result = service.create(address);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Updates An Address")
    public ResponseEntity<Object> update(@PathVariable int addressId, @RequestBody AddressUpdateDTO address) {
        if(addressId != address.getAddressId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<AddressResponseDTO> result = service.update(address);

        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
