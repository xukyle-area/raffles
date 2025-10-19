package com.gantenx.raffles.model.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.gantenx.raffles.model.entity.FlinkTableDDL;
import com.gantenx.raffles.model.entity.FlinkTableDDLExample;
import com.gantenx.raffles.model.mapper.FlinkTableDDLMapper;
import com.gantenx.raffles.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TableDDLDao {

    @Autowired
    private FlinkTableDDLMapper flinkTableDDLMapper;

    public FlinkTableDDL selectByName(String tableName) {
        FlinkTableDDLExample example = new FlinkTableDDLExample();
        example.createCriteria().andTableNameEqualTo(tableName);
        return ListUtils.firstOrNull(flinkTableDDLMapper.selectByExample(example));
    }

    public List<FlinkTableDDL> selectByNames(List<String> tableNames) {
        FlinkTableDDLExample example = new FlinkTableDDLExample();
        example.createCriteria().andTableNameIn(tableNames);
        return flinkTableDDLMapper.selectByExample(example);
    }
}
