package com.library.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for add/edit book form.
 */
@Data
@NoArgsConstructor
public class BookDto {

    private Long id;          // null when creating, set when editing

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150)
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(max = 20)
    private String isbn;

    @Min(value = 1, message = "At least 1 copy required")
    private int totalCopies;

    @Size(max = 1000)
    private String description;

    private Integer publishedYear;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
