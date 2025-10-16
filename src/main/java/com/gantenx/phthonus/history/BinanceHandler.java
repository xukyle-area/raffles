package com.gantenx.phthonus.history;


import java.util.Objects;
import org.json.JSONArray;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import com.gantenx.phthonus.model.common.DayQuote;
import com.gantenx.phthonus.utils.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "binance.history", name = "enabled", havingValue = "true")
public class BinanceHandler extends HistoryQuoteHandler {

    @Override
    public void handleHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(Market.BINANCE, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (this.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleBinanceHistory start");
        Symbol[] symbols = Symbol.getAllSymbols();
        for (Symbol symbol : symbols) {
            try {
                log.info("handleBinanceHistory symbol:{}", symbol);
                Request request = new Request.Builder().url(Market.BINANCE.getUrl() + symbol.getSymbol()).get().build();
                String body = Objects.requireNonNull(client.newCall(request).execute().body()).string();
                for (int i = 0; i < 2; i++) {
                    JSONArray candle = new JSONArray(body).getJSONArray(i);
                    long time = candle.getLong(0);
                    if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                        DayQuote quote = new DayQuote(symbol, time, Market.BINANCE, candle.getString(4));
                        log.info("handle binance dayQuote: {}", quote);
                    } else {
                        log.error("time {} error. {}", time, symbol);
                    }
                }
            } catch (Exception e) {
                log.error("error during handle history quote. {}", symbol);
            }
        }
        executeRecordMap.put(Market.BINANCE, System.currentTimeMillis());
    }
}
