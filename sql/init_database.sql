-- ==================================================
-- Raffles 数据库初始化脚本
-- 描述: 创建规则引擎相关的数据表结构
-- 创建时间: $(date)
-- ==================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS raffles;

-- 使用数据库
USE raffles;

-- ==================================================
-- 类目表 - 用于规则分类管理
-- ==================================================
CREATE TABLE category (
    id          BIGINT AUTO_INCREMENT COMMENT '序号' PRIMARY KEY,
    name        VARCHAR(255) NOT NULL COMMENT '名称',
    parent_id   BIGINT DEFAULT 0 NULL COMMENT '父级ID，0表示根节点',
    lft         INT DEFAULT 1 NULL COMMENT '左值',
    rgt         INT DEFAULT 2 NULL COMMENT '右值',
    level       INT DEFAULT 1 NULL COMMENT '层级',
    is_delete   TINYINT DEFAULT 0 NULL COMMENT '是否删除',
    create_time DATETIME NULL COMMENT '创建时间',
    update_time DATETIME NULL COMMENT '更新时间',
    
    CONSTRAINT category_name_uindex UNIQUE (name)
) COMMENT '类目表';

-- ==================================================
-- 规则表 - 存储各种检查规则
-- ==================================================
CREATE TABLE rule (
    id          BIGINT AUTO_INCREMENT COMMENT '序号' PRIMARY KEY,
    name        VARCHAR(64) NOT NULL COMMENT '规则名称',
    category_id BIGINT DEFAULT 1 NULL COMMENT '类目ID',
    level       TINYINT DEFAULT 1 NULL COMMENT '规则级别 1-信息；2-低；3-中；4-高',
    `desc`      VARCHAR(255) NULL COMMENT '描述',
    tips        VARCHAR(255) NULL COMMENT '建议',
    is_delete   TINYINT DEFAULT 0 NULL COMMENT '是否删除',
    create_time DATETIME NULL COMMENT '创建时间',
    update_time DATETIME NULL COMMENT '更新时间',
    
    CONSTRAINT rule_name_uindex UNIQUE (name)
) COMMENT '规则表';

-- ==================================================
-- SQL 模板表 - 存储 SQL 检查模板
-- ==================================================
CREATE TABLE sql_template (
    id          BIGINT AUTO_INCREMENT COMMENT '序号' PRIMARY KEY,
    expression  VARCHAR(4096) NULL COMMENT 'SQL 模板',
    `desc`      VARCHAR(32) NULL COMMENT '描述',
    create_time DATETIME NULL COMMENT '创建时间',
    update_time DATETIME NULL COMMENT '更新时间'
) COMMENT 'SQL 模板表';

-- ==================================================
-- Flink 表 DDL 表 - 存储 Flink 数据表定义
-- ==================================================
CREATE TABLE compliance_flink_table_ddl (
    id         BIGINT AUTO_INCREMENT COMMENT '序号' PRIMARY KEY,
    table_name VARCHAR(64) NOT NULL COMMENT '表名',
    `desc`     VARCHAR(32) NULL COMMENT '描述',
    ddl_sql    VARCHAR(2048) NULL COMMENT '构建语句',
    params     VARCHAR(1024) NULL COMMENT '参数',
    created_at DATETIME NULL COMMENT '创建时间',
    updated_at DATETIME NULL COMMENT '更新时间'
) COMMENT 'Flink 表 DDL 定义表';

-- ==================================================
-- 插入初始数据
-- ==================================================

-- 插入根类目
INSERT INTO category (id, name, parent_id, lft, rgt, level, is_delete, create_time, update_time) 
VALUES (1, '根类目', 0, 1, 2, 1, 0, NOW(), NOW());

-- 插入示例规则类目
INSERT INTO category (name, parent_id, lft, rgt, level, is_delete, create_time, update_time) 
VALUES 
('开户规则', 1, 2, 3, 2, 0, NOW(), NOW()),
('风控规则', 1, 4, 5, 2, 0, NOW(), NOW()),
('合规规则', 1, 6, 7, 2, 0, NOW(), NOW());

-- ==================================================
-- 查看创建的表
-- ==================================================
SHOW TABLES;