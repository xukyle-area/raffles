package com.gantenx.raffles.biz;

import com.gantenx.raffles.biz.consists.BizType;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.biz.consists.Direction;
import com.gantenx.raffles.util.GsonUtils;
import com.gantenx.raffles.util.TypesafeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BizConfigManager {
    private static final Map<BizType, BizConfig> BIZ_CONFIG_MAP = new ConcurrentHashMap<>();
    private static final Map<String, BizType> BIZ_TYPE_MAP = new ConcurrentHashMap<>();
    private static final String CMP_PREFIX = "cmp-biz.";

    static {
        Arrays.stream(BizType.values()).forEach(bizType -> {
            String configPath = CMP_PREFIX + bizType.getCode();
            BizConfig config = TypesafeUtils.getBean(BizConfig.class, configPath);
            log.info("Load biz config: {} -> {}", bizType, GsonUtils.toJson(config));
            if (!config.isEnable()) {
                log.info("Skip biz config: {} -> {}", bizType, GsonUtils.toJson(config));
                return;
            }
            BIZ_CONFIG_MAP.put(bizType, config);
            BIZ_TYPE_MAP.put(config.getCategory(), bizType);
        });
    }

    public static BizConfig.SourceConfig getSourceConfig(BizType bizType) {
        return BIZ_CONFIG_MAP.get(bizType).getSourceConfig();
    }

    public static BizConfig.SinkConfig getSinkConfig(BizType bizType) {
        return BIZ_CONFIG_MAP.get(bizType).getSinkConfig();
    }

    public static String getCategory(BizType bizType) {
        return BIZ_CONFIG_MAP.get(bizType).getCategory();
    }

    public static boolean isBatch(BizType bizType) {
        return BIZ_CONFIG_MAP.get(bizType).isBatch();
    }

    public static Set<BizType> getActiveBizTypes() {
        return BIZ_CONFIG_MAP.keySet();
    }

    public static BizType getBizType(String category) {
        return BIZ_TYPE_MAP.get(category);
    }

    public static Map<Direction, DataSourceType> getDataSourceType(BizType bizType) {
        BizConfig bizConfig = BIZ_CONFIG_MAP.get(bizType);
        Map<Direction, DataSourceType> map = new HashMap<>();
        BizConfig.SourceConfig source = bizConfig.getSourceConfig();
        map.put(Direction.SOURCE, DataSourceType.fromCode(source.getType()));
        BizConfig.SinkConfig sinkConfig = bizConfig.getSinkConfig();
        map.put(Direction.SINK, DataSourceType.fromCode(sinkConfig.getType()));
        return map;
    }
}
