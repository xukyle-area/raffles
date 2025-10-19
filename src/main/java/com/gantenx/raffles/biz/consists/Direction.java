package com.gantenx.raffles.biz.consists;

public enum Direction {
    SOURCE(1, "input"), SINK(2, "output");

    private final int id;
    private final String code;

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    Direction(int id, String code) {
        this.id = id;
        this.code = code;
    }
}
