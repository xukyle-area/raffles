package com.gantenx.raffles.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.gantenx.raffles.model.entity.SqlTemplate;
import com.gantenx.raffles.model.entity.SqlTemplateExample;
import com.gantenx.raffles.model.mapper.SqlTemplateMapper;
import com.gantenx.raffles.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SqlTemplateDao {

    @Autowired
    private SqlTemplateMapper sqlTemplateMapper;

    public SqlTemplate selectById(int id) {
        SqlTemplateExample example = new SqlTemplateExample();
        example.createCriteria().andIdEqualTo(id);
        return ListUtils.firstOrNull(sqlTemplateMapper.selectByExample(example));
    }
}
