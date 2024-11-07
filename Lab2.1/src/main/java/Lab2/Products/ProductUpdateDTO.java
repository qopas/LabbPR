package Lab2.Products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateDTO {
    private String name;
    private Double price;
    private String productLink;
    private Double monthlyCreditPrice;
}