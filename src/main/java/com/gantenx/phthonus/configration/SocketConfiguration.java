package com.gantenx.phthonus.configration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.socket.SocketTask;

@Configuration
public class SocketConfiguration {

    // @Bean
    // public SocketTask binanceSocketTask() {
    // return new SocketTask(Market.BINANCE);
    // }

    // @Bean
    // public SocketTask cryptoSocketTask() {
    // return new SocketTask(Market.CRYPTO_COM);
    // }

    @Bean
    public SocketTask hashSocketTask() {
        return new SocketTask(Market.HASHKEY);
    }
}
