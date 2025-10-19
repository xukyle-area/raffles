package com.gantenx.raffles.model.mapper;

import com.gantenx.raffles.model.entity.SqlTemplate;
import com.gantenx.raffles.model.entity.SqlTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlTemplateMapper {
    long countByExample(SqlTemplateExample example);

    int deleteByExample(SqlTemplateExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SqlTemplate row);

    int insertSelective(SqlTemplate row);

    List<SqlTemplate> selectByExample(SqlTemplateExample example);

    SqlTemplate selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") SqlTemplate row, @Param("example") SqlTemplateExample example);

    int updateByExample(@Param("row") SqlTemplate row, @Param("example") SqlTemplateExample example);

    int updateByPrimaryKeySelective(SqlTemplate row);

    int updateByPrimaryKey(SqlTemplate row);
}