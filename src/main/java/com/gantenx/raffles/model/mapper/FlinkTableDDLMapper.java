package com.gantenx.raffles.model.mapper;

import com.gantenx.raffles.model.entity.FlinkTableDDL;
import com.gantenx.raffles.model.entity.FlinkTableDDLExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FlinkTableDDLMapper {
    long countByExample(FlinkTableDDLExample example);

    int deleteByExample(FlinkTableDDLExample example);

    int deleteByPrimaryKey(Long id);

    int insert(FlinkTableDDL row);

    int insertSelective(FlinkTableDDL row);

    List<FlinkTableDDL> selectByExample(FlinkTableDDLExample example);

    FlinkTableDDL selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") FlinkTableDDL row, @Param("example") FlinkTableDDLExample example);

    int updateByExample(@Param("row") FlinkTableDDL row, @Param("example") FlinkTableDDLExample example);

    int updateByPrimaryKeySelective(FlinkTableDDL row);

    int updateByPrimaryKey(FlinkTableDDL row);
}