package com.eg.spiderlistenrequest.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eg.spiderlistenrequest.spider.Spider;

/**
 * @time 2020-02-12 16:51
 */
public class WsUtil {
    public static String STATE_READY = "ready";
    public static String extensionState = "";

    /**
     * 收到来自插件的消息
     *
     * @param json
     */
    public static void onReceive(String json) {
        WsMessage wsMessage = JSON.parseObject(json, WsMessage.class);
        String cmd = wsMessage.getCmd();
        if (cmd.equals("cmd")) {
            handleCmd(wsMessage);
        } else if (cmd.equals("url")) {
            handleUrl(wsMessage);
        }
    }

    /**
     * 处理cmd
     *
     * @param wsMessage
     */
    private static void handleCmd(WsMessage wsMessage) {
        String message = wsMessage.getMessage();
        //插件已就绪
        if (message.equals("ready")) {
            extensionState = STATE_READY;
        }
    }

    /**
     * 处理url
     *
     * @param wsMessage
     */
    private static void handleUrl(WsMessage wsMessage) {
        Spider.onReceiveUrlCallback(wsMessage);
    }
}
