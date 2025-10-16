package com.gantenx.phthonus.enums;

public enum Symbol {
    BTC_USDT(1, "BTC", "USDT"),
    ETH_USDT(2, "ETH", "USDT"),
    DOGE_USDT(3, "DOGE", "USDT"),
    SOL_USDT(4, "SOL", "USDT"),
    USDT_USD(5, "USDT", "USD");

    private final int id;
    private final String base;
    private final String quote;

    Symbol(int id, String base, String quote) {
        this.id = id;
        this.base = base;
        this.quote = quote;
    }

    public int getId() {
        return id;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    public String getSymbol() {
        return base + quote;
    }

    public String getSymbolWithDot() {
        return base + "." + quote;
    }

    public String getSymbolWithSubline() {
        return base + "_" + quote;
    }

    public static Symbol findById(int id) {
        for (Symbol s : Symbol.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public static Symbol findBySymbolWithoutDot(String symbol) {
        for (Symbol s : Symbol.values()) {
            if (s.getSymbol().equalsIgnoreCase(symbol)) {
                return s;
            }
        }
        return null;
    }

    public static Symbol findBySymbolWithSubline(String symbol) {
        for (Symbol s : Symbol.values()) {
            if (s.getSymbolWithSubline().equalsIgnoreCase(symbol)) {
                return s;
            }
        }
        return null;
    }

    public static Symbol findBySymbol(String symbol) {
        for (Symbol s : Symbol.values()) {
            if (s.getSymbolWithDot().equalsIgnoreCase(symbol)) {
                return s;
            }
        }
        return null;
    }

    public static Symbol[] getAllSymbols() {
        return Symbol.values();
    }
}
