package com.gantenx.raffles.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.biz.BizConfig;
import com.gantenx.raffles.biz.BizConfigManager;
import com.gantenx.raffles.biz.consists.BizType;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.model.dao.TableDDLDao;
import com.gantenx.raffles.model.entity.FlinkTableDDL;
import com.gantenx.raffles.util.ClassToMapConverter;
import com.gantenx.raffles.util.GsonUtils;
import com.gantenx.raffles.util.SQLTableExtractor;
import com.gantenx.raffles.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MysqlSource implements SourceService {

    @Autowired
    private TableDDLDao tableDDLDao;

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MYSQL;
    }

    @Override
    public void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, RuleFlinkSql rule) {
        BizType bizType = rule.getBizType();
        BizConfig.SourceConfig sourceConfig = BizConfigManager.getSourceConfig(bizType);
        this.checkType(sourceConfig);

        String executableSql = rule.getExecutableSql();
        List<String> ddls = this.getDDLs(executableSql, sourceConfig.getMysql());
        ddls.forEach(ste::executeSql);
    }

    public List<String> getDDLs(@Nonnull String executableSql, BizConfig.Mysql mysqlConfig) {
        List<String> ddls = new ArrayList<>();
        List<String> tableNames = new ArrayList<>(SQLTableExtractor.extractExternalTables(executableSql));
        Map<String, Object> mysqlParamMap = ClassToMapConverter.convertToMap(mysqlConfig);
        List<FlinkTableDDL> ddlList = tableDDLDao.selectByNames(tableNames);

        for (FlinkTableDDL table : ddlList) {
            Map<String, Object> paramMap = GsonUtils.toMap(table.getParams());
            paramMap.putAll(mysqlParamMap);
            ddls.add(SqlUtils.getExecutableSql(table.getDdlSql(), paramMap));
        }
        return ddls;
    }
}
