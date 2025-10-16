package com.gantenx.phthonus.history;

import static com.gantenx.phthonus.utils.TimestampUtils.MILLIS_OF_ONE_HOUR;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.utils.HttpFactory;
import com.gantenx.phthonus.utils.ThreadPool;
import com.gantenx.phthonus.utils.TimestampUtils;
import okhttp3.OkHttpClient;

@Component
public abstract class HistoryQuoteHandler {
    private final static int PERIOD = 20 * 60 * 1000;
    protected static final OkHttpClient client = HttpFactory.getInstance().getSharedClient();
    protected static final Map<Market, Long> executeRecordMap = new HashMap<>();

    @PostConstruct
    public void init() {
        this.handleHistory();
        this.scheduleConnect();
    }

    /**
     * 判断给定的时间戳是否在当天的1am UTC之后
     *
     * @param lastExecuteTime 时间戳
     * @return 如果在当天1am UTC之后返回true，否则返回false
     */
    protected boolean isAfter1amUTC(long lastExecuteTime) {
        // 上一个1am UTC的时间戳
        long time = TimestampUtils.midnightTimestampToday() + MILLIS_OF_ONE_HOUR;
        return lastExecuteTime > time;
    }

    public abstract void handleHistory();

    public void scheduleConnect() {
        long initialDelay = TimestampUtils.millisecondsUntilUTCHour(1);
        ThreadPool.scheduleWithFixedDelay(this::handleHistory, initialDelay, PERIOD, TimeUnit.SECONDS);
    }
}
