package com.gantenx.raffles.constants.enums;

import lombok.Getter;

@Getter
public enum Market {
    BINANCE(1, "wss://stream.binance.com:443/stream", "https://api.binance.com/api/v3/klines?interval=1d&limit=3&symbol=", "com.gantenx.phthonus.socket.BinanceSocketClient"),
    CRYPTO_COM(2, "wss://stream.crypto.com/v2/market", "https://api.crypto.com/v2/public/get-candlestick?timeframe=1d&instrument_name=", "com.gantenx.phthonus.socket.CryptoSocketClient"),
    HASHKEY(3, "wss://stream-pro.sim.hashkeydev.com/quote/ws/v1", "", "com.gantenx.phthonus.socket.HashkeySocketClient"),
    UNRECOGNIZED(0, "", "", "");

    private final int value;
    private final String url;
    private final String webSocketUrl;
    private final String socketClient;

    Market(int value, String webSocketUrl, String url, String socketClient) {
        this.url = url;
        this.value = value;
        this.webSocketUrl = webSocketUrl;
        this.socketClient = socketClient;
    }
}