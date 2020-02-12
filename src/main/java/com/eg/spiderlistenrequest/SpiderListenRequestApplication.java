package com.eg.spiderlistenrequest;

import com.eg.spiderlistenrequest.spider.Spider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpiderListenRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpiderListenRequestApplication.class, args);

        Spider.init();
        Spider.run();
    }

}
