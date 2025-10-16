package com.gantenx.phthonus.model.common;

import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RealTimeQuote {
    private Symbol symbol;
    private long timestamp;
    private Market market;
    private String last;
    private String ask;
    private String bid;
}
