package com.gantenx.phthonus.model.websocket.crypto;

import lombok.Data;

@Data
public class Result {

    private String subscription;

    private Dat[] data;
}
