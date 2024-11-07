package Lab2.Products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create a new product
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok("Product with id " + createdProduct.getId() + " was created successfully.");
    }

    // Get all products
    @GetMapping
    public List<Product> getAllProducts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "5") int limit) {
        return productService.getAllProducts(offset, limit);
    }

    // Get a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a product
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable("id") Long id, @RequestBody ProductUpdateDTO productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok("Product with id " + updatedProduct.getId() + " was updated successfully.");
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product with id " + id + " was deleted");
    }
    // File upload
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Process the file (e.g., read JSON content)
            String result = productService.processUploadedFile(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }
}
