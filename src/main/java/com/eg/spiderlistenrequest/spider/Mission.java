package com.eg.spiderlistenrequest.spider;

import lombok.Data;

/**
 * @time 2020-02-12 20:23
 */
@Data
public class Mission {
    private String name;
    private String format = "m4a";
    private String pageUrl;
    private String downloadUrl;
    private boolean finished = false;
}
