package com.gantenx.raffles.model;

import com.gantenx.raffles.biz.consists.BizType;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleFlinkSql implements Serializable {
    private static final long serialVersionUID = 7924929543878502331L;
    /**
     * rule 的自增 id
     */
    private Integer id;
    /**
     * 用作rule的名字
     */
    private String code;
    private String executableSql;
    private Integer categoryId;
    private String categoryCode;
    private String name;
    private Integer version;
    private String params;
    private String paramsDesc;
    private BizType bizType;
}
