package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CategoryDto;

import java.util.List;

public class CategoryDtoConverter {
    public static CategoryDto createCategoryDtoFromCategory(Category category) {
        return new CategoryDto(category.getName());
    }

    public static List<CategoryDto> createCategoryDtoListFromCategoryList(List<Category> categories) {
        return categories.stream().map(CategoryDtoConverter::createCategoryDtoFromCategory).toList();
    }
}
