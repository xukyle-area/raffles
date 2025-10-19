package com.gantenx.raffles.model;

import java.io.Serializable;
import com.gantenx.raffles.biz.consists.BizType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleFlinkSql implements Serializable {
    private static final long serialVersionUID = 7924929543878502331L;
    /**
     * rule 的自增 id
     */
    private int id;
    /**
     * 用作rule的名字
     */
    private String name;
    private String executableSql;
    private int categoryId;
    private String categoryName;
    private Integer version;
    private String params;
    private String paramsDesc;
    private BizType bizType;
}
