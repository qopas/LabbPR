package Lab2.Products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts(int offset, int limit) {
        // Validate limit
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        // Calculate the page number
        int pageNumber = offset / limit;

        // Calculate the starting index
        int startingIndex = offset;
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Product> page = productRepository.findAll(pageable);
        List<Product> products = page.getContent();
        if (startingIndex < products.size()) {
            return products.stream()
                    .skip(startingIndex)
                    .limit(limit)
                    .toList();
        }

        return List.of();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateDTO productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update only the fields that are provided in the DTO
        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getPrice() != null) {
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getProductLink() != null) {
            product.setProductLink(productDetails.getProductLink());
        }
        if (productDetails.getMonthlyCreditPrice() != null) {
            product.setMonthlyCreditPrice(productDetails.getMonthlyCreditPrice());
        }

        return productRepository.save(product);
    }
    public String processUploadedFile(MultipartFile file) throws Exception {
        // Validate file type
        if (!file.getContentType().equals("application/json")) {
            throw new Exception("File type must be JSON.");
        }

        // Read the contents of the file
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return "Uploaded file content: " + content.toString();
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}