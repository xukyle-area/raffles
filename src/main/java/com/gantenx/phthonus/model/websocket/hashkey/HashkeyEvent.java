package com.gantenx.phthonus.model.websocket.hashkey;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashkeyEvent {

    private String symbol;
    private String symbolName;
    private String topic;
    private Params params;
    private Data[] data;
    private boolean f;
    private long sendTime;
    private String channelId;
    private boolean shared;
    private String id;

}
