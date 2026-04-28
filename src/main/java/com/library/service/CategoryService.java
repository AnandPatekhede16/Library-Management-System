package com.library.service;

import com.library.entity.Category;
import com.library.exception.LibraryException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for category management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        List<Category> categories = categoryRepository.findAll();
        // Initialize the lazy books collection to prevent LazyInitializationException in the view
        categories.forEach(cat -> cat.getBooks().size());
        return categories;
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new LibraryException("Category '" + name + "' already exists.");
        }
        return categoryRepository.save(Category.builder()
            .name(name)
            .description(description)
            .build());
    }

    public Category updateCategory(Long id, String name, String description) {
        Category cat = findById(id);
        cat.setName(name);
        cat.setDescription(description);
        return categoryRepository.save(cat);
    }

    public void deleteCategory(Long id) {
        Category cat = findById(id);
        if (!cat.getBooks().isEmpty()) {
            throw new LibraryException(
                "Cannot delete category '" + cat.getName() + "' — it still has books assigned.");
        }
        categoryRepository.delete(cat);
    }

    @Transactional(readOnly = true)
    public long count() {
        return categoryRepository.count();
    }
}
