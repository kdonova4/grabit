package com.kdonova4.grabit.domain;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kdonova4.grabit.data.ImageRepository;
import com.kdonova4.grabit.data.ProductRepository;
import com.kdonova4.grabit.domain.mapper.ImageMapper;
import com.kdonova4.grabit.model.entity.Image;
import com.kdonova4.grabit.model.dto.ImageCreateDTO;
import com.kdonova4.grabit.model.dto.ImageResponseDTO;
import com.kdonova4.grabit.model.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository repository;
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    public ImageService(ImageRepository repository, ProductRepository productRepository, Cloudinary cloudinary) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
    }

    public Map uploadFile(MultipartFile file) throws IOException {
        File uploadedFile = convertToFile(file); // Convert to java.io.File
        Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
        uploadedFile.delete(); // Clean up temp file
        return uploadResult;
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("upload-", multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
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

    public Result<ImageResponseDTO> create(MultipartFile file, int productId) {

        Result<ImageResponseDTO> result = new Result<>();

        Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty()) {
            result.addMessages("PRODUCT MUST EXIST", ResultType.NOT_FOUND);
            return result;
        }

        try {
            Map uploadResult = uploadFile(file);
            String imageUrl = (String) uploadResult.get("secure_url");

            Image image = new Image();
            image.setImageUrl(imageUrl);
            image.setProduct(product.get());

            Result<ImageResponseDTO> validate = validate(image);
            if(!validate.isSuccess()) {
                return validate;
            }

            image = repository.save(image);
            result.setPayload(ImageMapper.toResponseDTO(image));
        } catch (IOException e) {
            result.addMessages("UPLOAD FAILED: " + e.getMessage(), ResultType.INVALID);
        }

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
