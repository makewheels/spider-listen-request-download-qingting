package com.eg.spiderlistenrequest.ws;

import com.alibaba.fastjson.JSON;

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
        String message = wsMessage.getMessage();
        if (cmd.equals("cmd")) {
            handleCmd(message);
        } else if (cmd.equals("url")) {
            handleUrl(message);
        }
    }

    /**
     * 处理cmd
     *
     * @param cmd
     */
    private static void handleCmd(String cmd) {
        //插件已就绪
        if (cmd.equals("ready")) {
            extensionState = STATE_READY;
        }
    }

    /**
     * 处理url
     *
     * @param url
     */
    private static void handleUrl(String url) {
        if (url.startsWith("https://od.sign.qingting.fm/")) {
            System.out.println(url);
        }
    }
}
