package com.gantenx.raffles.biz.ongoingcdd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OngoingCddOutput {
    private static final long serialVersionUID = 344173889411245246L;
    private String customerId;
    private String userId;
    private String gapDay;
    private String daysThreshold;
    private String needCdd;
}
