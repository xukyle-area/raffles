-- 创建数据库
CREATE DATABASE IF NOT EXISTS raffles DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE raffles;

-- 规则表
CREATE TABLE IF NOT EXISTS `rule` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_id` INT COMMENT '类别ID',
    `code` VARCHAR(255) COMMENT '规则代码',
    `sql_template_id` INT COMMENT 'SQL模板ID',
    `params` TEXT COMMENT '参数（JSON格式）',
    `params_desc` TEXT COMMENT '参数描述',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `status` INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规则配置表';

-- SQL模板表
CREATE TABLE IF NOT EXISTS `sql_template` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `expression` TEXT NOT NULL COMMENT 'SQL表达式模板',
    `desc` VARCHAR(500) COMMENT '模板描述',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SQL模板表';

-- Flink表DDL配置表
CREATE TABLE IF NOT EXISTS `flink_table_ddl` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `table_name` VARCHAR(255) NOT NULL COMMENT 'Flink表名',
    `desc` VARCHAR(500) COMMENT '表描述',
    `ddl_sql` TEXT NOT NULL COMMENT 'Flink DDL语句',
    `params` TEXT COMMENT '参数配置（JSON格式）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Flink表DDL配置';

-- 插入 Flink 表 DDL（输入表和输出表）
INSERT INTO flink_table_ddl (table_name, `desc`, ddl_sql) VALUES
('calculate_input', '计算输入表', 
'CREATE TABLE calculate_input (
    numberFirst INT,
    numberSecond INT,
    proc_time AS PROCTIME()
) WITH (
    ''connector'' = ''kafka'',
    ''topic'' = ''calculate-input'',
    ''properties.bootstrap.servers'' = ''localhost:9092'',
    ''properties.group.id'' = ''calculate-group'',
    ''scan.startup.mode'' = ''latest-offset'',
    ''format'' = ''json''
)');

-- 插入 SQL 模板（计算规则）
INSERT INTO sql_template (expression, `desc`) VALUES
('SELECT 
    numberFirst * numberSecond AS multiplyResult,
    numberFirst + numberSecond AS addResult
FROM calculate_input', 
'计算乘法和加法结果');

-- 插入规则（关联模板和参数）
INSERT INTO rule (category_id, code, sql_template_id, params, params_desc, version, status) VALUES
(1, 'CALCULATE_DEMO', 1, '{}', '数字计算演示：加法和乘法', 1, 1);