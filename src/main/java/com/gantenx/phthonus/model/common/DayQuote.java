package com.gantenx.phthonus.model.common;

import org.jetbrains.annotations.NotNull;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DayQuote {
    private Symbol symbol;
    private long timestamp;
    @NotNull
    private Market market;
    @NotNull
    private String preClose;
}
