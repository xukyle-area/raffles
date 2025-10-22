package com.gantenx.raffles.model;

import java.io.Serializable;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlinkRule implements Serializable {
    private static final long serialVersionUID = 7924929543878502331L;
    private int id;
    private String name;
    private String executableSql;
    private int categoryId;
    private String categoryName;
    private Integer version;
    private String params;
    private String paramsDesc;
    private Category category;
    private CategoryConfig categoryConfig;
}
