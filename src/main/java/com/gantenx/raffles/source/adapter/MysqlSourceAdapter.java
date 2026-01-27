package com.gantenx.raffles.source.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.model.dao.TableDDLDao;
import com.gantenx.raffles.model.entity.FlinkTableDDL;
import com.gantenx.raffles.utils.ClassToMapConverter;
import com.gantenx.raffles.utils.GsonUtils;
import com.gantenx.raffles.utils.SQLTableExtractor;
import com.gantenx.raffles.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * MySQL Source适配器
 * 负责从MySQL注册数据源到Flink环境
 */
@Slf4j
@Service
public class MysqlSourceAdapter extends SourceAdapter {
    private static final long serialVersionUID = 1L;

    @Autowired
    private TableDDLDao tableDDLDao;

    @Override
    public DataType getDataType() {
        return DataType.MYSQL;
    }

    @Override
    public void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, FlinkRule rule) {
        CategoryConfig categoryConfig = rule.getCategoryConfig();
        CategoryConfig.DataTypeConfig sourceConfig = categoryConfig.getSourceConfig();
        this.checkType(sourceConfig);

        String executableSql = rule.getExecutableSql();
        List<String> ddls = this.getDDLs(executableSql, sourceConfig.getMysql());
        ddls.forEach(ddl -> {
            log.info("Executing MySQL DDL: {}", ddl);
            ste.executeSql(ddl);
        });
    }

    /**
     * 获取MySQL表的DDL语句列表
     */
    public List<String> getDDLs(@Nonnull String executableSql, CategoryConfig.Mysql mysqlConfig) {
        List<String> ddls = new ArrayList<>();
        List<String> tableNames = new ArrayList<>(SQLTableExtractor.extractExternalTables(executableSql));
        Map<String, Object> mysqlParamMap = ClassToMapConverter.convertToMap(mysqlConfig);
        List<FlinkTableDDL> ddlList = tableDDLDao.selectByNames(tableNames);

        for (FlinkTableDDL table : ddlList) {
            Map<String, Object> paramMap = new HashMap<>();

            // 如果表有自己的参数配置，先加载
            if (table.getParams() != null && !table.getParams().trim().isEmpty()) {
                Map<String, Object> tableParams = GsonUtils.toMap(table.getParams());
                if (tableParams != null) {
                    paramMap.putAll(tableParams);
                }
            }

            // MySQL 配置覆盖表参数
            paramMap.putAll(mysqlParamMap);

            ddls.add(SqlUtils.getExecutableSql(table.getDdlSql(), paramMap));
        }
        return ddls;
    }
}
