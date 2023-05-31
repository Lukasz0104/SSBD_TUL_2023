package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CategoryDTO;

import java.util.List;

public class CategoryDtoConverter {
    public static CategoryDTO createCategoryDtoFromCategory(Category category) {
        return new CategoryDTO(category.getName());
    }

    public static List<CategoryDTO> createCategoryDtoListFromCategoryList(List<Category> categories) {
        return categories.stream().map(CategoryDtoConverter::createCategoryDtoFromCategory).toList();
    }
}
