package com.eg.spiderlistenrequest.ws;

import lombok.Data;

/**
 * @time 2020-02-12 17:46
 */
@Data
public class WsMessage {
    private String cmd;
    private String message;
    private String tabUrl;
}
