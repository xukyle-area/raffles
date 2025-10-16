package com.gantenx.phthonus.model.websocket.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Dat {

    @JsonProperty("k")
    private String ask;
    @JsonProperty("b")
    private String bid;
    @JsonProperty("a")
    private String last;

}
