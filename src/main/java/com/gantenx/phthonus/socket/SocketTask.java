package com.gantenx.phthonus.socket;

import java.util.concurrent.TimeUnit;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.utils.ThreadPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketTask {
    private static final int INITIAL_DELAY = 5;
    private static final int FIXED_DELAY = 15;
    private BaseSocketClient currentClient = null;
    private final Market type;

    public SocketTask(Market type) {
        this.type = type;
        ThreadPool.scheduleWithFixedDelay(this::checkAndHandle, INITIAL_DELAY, FIXED_DELAY, TimeUnit.SECONDS);
    }

    private void checkAndHandle() {
        log.info("{} - checkAndHandle", this.getClass().getSimpleName());
        if (currentClient == null || !currentClient.isOpen()) {
            this.reconnect();
        } else {
            currentClient.subscription();
        }
    }

    public synchronized void reconnect() {
        BaseSocketClient nextClient;
        try {
            log.info("try to connect websocket of {}", type);
            String socketClient = type.getSocketClient();
            Class<?> clazz = Class.forName(socketClient);
            if (!BaseSocketClient.class.isAssignableFrom(clazz)) {
                log.error("Class {} is not a subclass of BaseSocketClient", socketClient);
                return;
            }
            nextClient = (BaseSocketClient) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Error creating websocket client: {}", e.getMessage(), e);
            return;
        }
        log.info("build websocket client success, websocket of {}", type);
        try {
            if (currentClient != null) {
                currentClient.close();
            }
            nextClient.connect();
            currentClient = nextClient;
        } catch (Exception e) {
            log.error("Error reconnecting: {}", e.getMessage(), e);
        }
    }
}
