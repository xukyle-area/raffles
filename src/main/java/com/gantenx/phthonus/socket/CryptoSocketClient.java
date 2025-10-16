package com.gantenx.phthonus.socket;


import static com.gantenx.phthonus.constants.Constant.CHANNELS;
import static com.gantenx.phthonus.constants.Constant.SUBSCRIBE;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import com.gantenx.phthonus.model.common.RealTimeQuote;
import com.gantenx.phthonus.model.websocket.crypto.CryptoEvent;
import com.gantenx.phthonus.model.websocket.crypto.CryptoRequest;
import com.gantenx.phthonus.model.websocket.crypto.Dat;
import com.gantenx.phthonus.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CryptoSocketClient extends BaseSocketClient {

    public CryptoSocketClient() throws URISyntaxException {
        super(Market.CRYPTO_COM.getWebSocketUrl());
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("public/heartbeat")) {
            CryptoRequest request = JsonUtils.readValue(message, CryptoRequest.class);
            request.setMethod("public/respond-heartbeat");
            request.setNonce(System.currentTimeMillis());
            this.send(JsonUtils.toJson(request));
        } else {
            super.onMessage(message);
        }
    }

    @Override
    protected Consumer<String> getCallback() {
        return text -> {
            try {
                CryptoEvent cryptoEvent = JsonUtils.readValue(text, CryptoEvent.class);
                Dat data = cryptoEvent.getResult().getData()[0];
                String symbol = cryptoEvent.getResult().getSubscription();
                symbol = symbol.replace("ticker.", "");
                Symbol symbolEnum = Symbol.findBySymbolWithSubline(symbol);
                RealTimeQuote realTimeQuote = new RealTimeQuote();
                realTimeQuote.setSymbol(symbolEnum);
                realTimeQuote.setTimestamp(System.currentTimeMillis());
                realTimeQuote.setAsk(data.getAsk());
                realTimeQuote.setLast(data.getLast());
                realTimeQuote.setBid(data.getBid());
                realTimeQuote.setMarket(Market.CRYPTO_COM);

                
            } catch (Exception e) {
                log.error("error during sink.{}", text, e);
            }
        };
    }

    @Override
    protected String buildSubscription(Symbol[] symbols) {
        ArrayList<String> channels = new ArrayList<>();
        for (Symbol s : symbols) {
            String x = "ticker." + s.getBase() + "_" + s.getQuote();
            channels.add(x);
        }
        Map<String, Object> channelsMap = Collections.singletonMap(CHANNELS, channels);
        CryptoRequest request = new CryptoRequest(id++, SUBSCRIBE, channelsMap, System.currentTimeMillis());
        return JsonUtils.toJson(request);
    }
}
