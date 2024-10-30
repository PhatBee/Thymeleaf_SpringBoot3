package vn.phatbee.thymeleaf_springboot3.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @NotEmpty(message = "Không được bỏ trống")
    private String name;
   

    private Boolean isEdit = false;

}
