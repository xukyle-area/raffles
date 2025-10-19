package com.gantenx.raffles.biz.openaccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountOutput {

    private static final long serialVersionUID = 3351123132946647886L;

    private String account;

    private String ruleCode;

    private String ruleExpr;

    private String ruleType;

    private String customerId;

    private String hitDesc;

    private double score;

    private long createTime;
}
