package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.util.List;

@Local
public interface CategoryManagerLocal extends CommonManagerInterface {
    List<Category> getAllCategories() throws AppBaseException;

    Page<Rate> getCategoryRates(Long categoryId, int page, int pageSize) throws AppBaseException;
}
