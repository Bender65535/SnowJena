package cn.yueshutong.core.config;

import cn.yueshutong.core.exception.SnowJeanException;
import cn.yueshutong.core.ticket.TicketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 单例模式
 * DCL双检查锁
 */
public class RateLimiterConfig {
    private static RateLimiterConfig rateLimiterConfig; //单例
    private static Logger logger = LoggerFactory.getLogger(RateLimiterConfig.class);

    private TicketServer ticketServer; //发票服务器
    private ScheduledExecutorService scheduledThreadExecutor; //调度线程池
    private ThreadPoolExecutor singleThread = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());//单例线程池

    //Ticket server interface
    public static String monitor = "monitor";
    public static String heart = "heart";
    public static String token = "token";

    private RateLimiterConfig() {
        //禁止new实例
    }

    public static RateLimiterConfig getInstance() {
        if (rateLimiterConfig == null) {
            synchronized (RateLimiterConfig.class) {
                if (rateLimiterConfig == null) {
                    rateLimiterConfig = new RateLimiterConfig();
                    logger.info("SnowJean");
                }
            }
        }
        return rateLimiterConfig;
    }

    public ScheduledExecutorService getScheduledThreadExecutor() {
        if (this.scheduledThreadExecutor == null) {
            synchronized (this) {
                if (this.scheduledThreadExecutor == null) {
                    setScheduledThreadExecutor(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));
                }
            }
        }
        return this.scheduledThreadExecutor;
    }

    public void setScheduledThreadExecutor(ScheduledExecutorService scheduledThreadExecutor) {
        this.scheduledThreadExecutor = scheduledThreadExecutor;
    }

    public TicketServer getTicketServer() {
        if (ticketServer == null) {
            throw new SnowJeanException("error: ticketServer == null");
        }
        return ticketServer;
    }

    public void setTicketServer(Map<String, Integer> ip) {
        if (ip.size() < 1) {
            throw new SnowJeanException("ip.size()<1 is not pass!");
        }
        if (this.ticketServer == null) {
            synchronized (this) {
                if (this.ticketServer == null) {
                    this.ticketServer = new TicketServer();
                }
            }
        }
        this.ticketServer.setServer(ip);
    }

    public ThreadPoolExecutor getSingleThread() {
        return singleThread;
    }

}
