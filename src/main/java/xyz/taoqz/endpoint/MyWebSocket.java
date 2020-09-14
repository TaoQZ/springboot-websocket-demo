package xyz.taoqz.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.taoqz.config.HttpSessionConfigurator;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author T
 */

/**
 * 每个连接会话都会生成一个MyWebSocket
 * ws 接受websocket请求路径
 */
@ServerEndpoint(value = "/ws",configurator = HttpSessionConfigurator.class)
@Component
public class MyWebSocket {

    /** 保存所有在线socket连接 */
    private static Map<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();
    public static Map<String, MyWebSocket> httpSessionMyWebSocketMap = new ConcurrentHashMap<>();

    /** 记录当前在线数目 */
    private static int count = 0;

    /**
     * 当前连接（每个websocket连入都会创建一个MyWebSocket实例 */
    private Session session;

    private HttpSession httpSession;

    private Logger log = LoggerFactory.getLogger(this.getClass());


    /** 处理连接建立 */
    @OnOpen
    public void onOpen(Session session,EndpointConfig endpointConfig) {
        this.session = session;
        webSocketMap.put(session.getId(), this);
        addCount();

        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
        httpSessionMyWebSocketMap.put(httpSession.getId(),this);
        log.info("新的连接加入：{}", session.getId());
    }

    /**
     * 接受消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端{}消息：{}", session.getId(), message);
        try {
            this.sendMessage("收到消息：" + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *处理错误
     */
    @OnError
    public void onError(Throwable error, Session session) {
        error.printStackTrace();
        log.info("发生错误{},{}", session.getId(), error.getMessage());
    }

    /**
     * 处理连接关闭
     */
    @OnClose
    public void onClose() {
        webSocketMap.remove(this.session.getId());
        httpSessionMyWebSocketMap.remove(httpSession.getId());
        reduceCount();
        log.info("连接关闭 id:{}", this.session.getId());
    }

    /**
     * 发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);//异步
    }

    /**
     * 群发消息
     * @param message
     */
    public static void broadcast(String message) {
        MyWebSocket.webSocketMap.forEach((k, v) -> {
            try {
                v.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 获取在线连接数目
     * @return
     */
    public static int getCount() {
        return count;
    }

    /**
     * 操作count，使用synchronized确保线程安全
     */
    public static synchronized void addCount() {
        MyWebSocket.count++;
    }

    public static synchronized void reduceCount() {
        MyWebSocket.count--;
    }

}
