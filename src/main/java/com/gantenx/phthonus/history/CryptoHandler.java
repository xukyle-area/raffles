package com.gantenx.phthonus.history;

import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import com.gantenx.phthonus.model.common.DayQuote;
import com.gantenx.phthonus.utils.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.ResponseBody;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "crypto.history", name = "enabled", havingValue = "true")
public class CryptoHandler extends HistoryQuoteHandler {

    @Override
    public void handleHistory() {
        Long lastExecuteTime = executeRecordMap.getOrDefault(Market.CRYPTO_COM, 0L);
        log.info("lastExecuteTime:{}", lastExecuteTime);
        if (this.isAfter1amUTC(lastExecuteTime)) {
            log.info("the task has been executed, lastExecuteTime:{}", lastExecuteTime);
            return;
        }
        log.info("handleCryptoHistory start");

        Symbol[] symbols = Symbol.getAllSymbols();
        for (Symbol symbol : symbols) {
            try {
                Request request = new Request.Builder().url(Market.CRYPTO_COM.getUrl() + symbol.getSymbolWithSubline())
                        .get().build();
                ResponseBody responseBody = client.newCall(request).execute().body();
                String body = Objects.requireNonNull(responseBody).string();
                JSONArray data = new JSONObject(body).getJSONObject("result").getJSONArray("data");
                for (int i = 0; i < 2; i++) {
                    JSONObject candle = data.getJSONObject(data.length() - 3 + i);
                    long time = candle.getLong("t");
                    if (time == TimestampUtils.midnightTimestampBefore(2 - i)) {
                        DayQuote quote = new DayQuote(symbol, time, Market.CRYPTO_COM, candle.getString("c"));
                        log.info("handle crypto dayQuote:{}", quote);
                    } else {
                        log.error("handle crypto dayQuote for {} error.", symbol);
                    }
                }
                executeRecordMap.put(Market.CRYPTO_COM, System.currentTimeMillis());
            } catch (Exception e) {
                log.error("error during handle history {} quote.", symbol, e);
            }
        }
    }
}
