package com.gantenx.raffles.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.gantenx.raffles.model.entity.Category;
import com.gantenx.raffles.model.entity.CategoryExample;
import com.gantenx.raffles.model.mapper.CategoryMapper;
import com.gantenx.raffles.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CategoryDao {

    @Autowired
    private CategoryMapper categoryMapper;

    public Category selectByCategoryId(int categoryId) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andIdEqualTo(categoryId);
        return ListUtils.firstOrNull(categoryMapper.selectByExample(categoryExample));
    }
}
