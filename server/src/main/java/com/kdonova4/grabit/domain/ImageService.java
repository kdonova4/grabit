package com.kdonova4.grabit.domain;

import com.kdonova4.grabit.data.ImageRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.ImageMapper;
import com.kdonova4.grabit.model.Image;
import com.kdonova4.grabit.model.ImageCreateDTO;
import com.kdonova4.grabit.model.ImageResponseDTO;
import com.kdonova4.grabit.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository repository;
    private final ProductRepository productRepository;

    public ImageService(ImageRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public List<Image> findAll() {
        return repository.findAll();
    }

    public List<Image> findByProduct(Product product) {
        return repository.findByProduct(product);
    }

    public Optional<Image> findById(int id) {
        return repository.findById(id);
    }

    public Result<ImageResponseDTO> create(ImageCreateDTO imageCreateDTO) {

        Product product = productRepository.findById(imageCreateDTO.getProductId()).orElse(null);
        Image image = ImageMapper.toImage(imageCreateDTO, product);

        Result<ImageResponseDTO> result = validate(image);

        if(!result.isSuccess())
            return result;

        if(image.getImageId() != 0) {
            result.addMessages("ImageId CANNOT BE SET for 'add' operation", ResultType.INVALID);
            return result;
        }

        image = repository.save(image);
        result.setPayload(ImageMapper.toResponseDTO(image));
        return result;
    }

    public boolean deleteById(int id) {
        if(repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private Result<ImageResponseDTO> validate(Image image) {
        Result<ImageResponseDTO> result = new Result<>();

        if(image == null) {
            result.addMessages("IMAGE CANNOT BE NULL", ResultType.INVALID);
            return result;
        }

        if(image.getProduct() == null || image.getProduct().getProductId() <= 0) {
            result.addMessages("PRODUCT IS REQUIRED", ResultType.INVALID);
        } else {
            Optional<Product> product = productRepository.findById(image.getProduct().getProductId());
            if(product.isEmpty()) {
                result.addMessages("PRODUCT MUST EXIST", ResultType.INVALID);
            }
        }

        if(image.getImageUrl() == null || image.getImageUrl().isBlank()) {
            result.addMessages("IMAGE URL CANNOT BE NULL OR BLANK", ResultType.INVALID);
        }

        return result;
    }
}
