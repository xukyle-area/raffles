package com.gantenx.raffles.biz.dormant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DormantOutput {
    private static final long serialVersionUID = 346273883513113246L;
    private String event;
    private String eventTime;
    private String account;
}
