package com.gantenx.phthonus.model.websocket.crypto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoRequest {
    private Long id;
    private String method;
    private Map<String, Object> params;
    private Long nonce;
}
