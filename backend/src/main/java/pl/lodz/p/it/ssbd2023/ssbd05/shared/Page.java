package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Page<T> {

    private List<T> data;

    private Long totalSize;

    private int pageSize;

    private int currentPage;

}