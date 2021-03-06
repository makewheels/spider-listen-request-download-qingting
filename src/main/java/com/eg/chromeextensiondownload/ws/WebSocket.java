package com.eg.chromeextensiondownload.ws;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @time 2020-02-12 15:40
 */
@ServerEndpoint("/{userId}")
@Component
public class WebSocket {
    private static Logger log = Logger.getLogger(WebSocket.class);
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    // concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";
    private static Map<String, WebSocket> webSocketMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        webSocketSet.add(this);
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
            addOnlineCount();
        }
        log.info(userId + "加入");
        log.info("当前在线人数为：" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); // 从set中删除
        subOnlineCount(); // 在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
//        log.info("onMessage " + userId + ": " + message);
        WsUtil.onReceive(message);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        // this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
        for (WebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 发送自定义消息
     * 按userid发送消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到：" + userId + "，报文：" + message);
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    /**
     * 获取在线客户端数量
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 添加线上链接的客户端数量
     */
    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    /**
     * 客户端下载是调用将在线数量减1
     */
    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }
}
