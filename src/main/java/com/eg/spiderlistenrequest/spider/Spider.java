package com.eg.spiderlistenrequest.spider;

import com.eg.spiderlistenrequest.ws.WsMessage;
import com.eg.spiderlistenrequest.ws.WsUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫
 *
 * @time 2020-02-12 18:05
 */
public class Spider {
    public static WebDriver driver;

    public static void init() {
        //安装插件
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--mute-audio");
        String classpath = Spider.class.getResource("/").getPath();
        chromeOptions.addExtensions(new File(classpath, "HelloChromeExtension.crx"));
        driver = new ChromeDriver(chromeOptions);
//        driver = new PhantomJSDriver(chromeOptions);

        //窗口最大化
//        driver.manage().window().maximize();
    }

    /**
     * 等待插件发来ready信息
     */
    private static void waitForExtensionReady() {
        int count = 1;
        //等待插件就绪
        while (WsUtil.extensionState.equals(WsUtil.STATE_READY) == false) {
            try {
                System.out.println("wait for extension... " + count);
                count++;
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //下载任务列表
    private static List<Mission> missionList = new ArrayList<>();

    /**
     * 初始化任务列表
     */
    private static void initMissionList() {
        Mission mission = new Mission();
        mission.setName("【魏武挥鞭】01大江东去");
        mission.setPageUrl("https://www.qingting.fm/channels/237072/programs/8397875/");
        missionList.add(mission);

        Mission mission1 = new Mission();
        mission1.setName("【魏武挥鞭】02真假曹操");
        mission1.setPageUrl("https://www.qingting.fm/channels/237072/programs/8397879/");
        missionList.add(mission1);
    }

    /**
     * 开始运行爬虫
     */
    public static void run() {
        //等待插件就绪
        waitForExtensionReady();
        System.out.println("extension ready");
        //插件已就绪，可以开始表演了
        initMissionList();
        //下载
        downloadSingle();
    }

    //是否监听ws发来的url
    private static boolean listening = false;

    /**
     * 下载单集
     */
    private static void downloadSingle() {
        //任务集合判空，程序最终在这里结束
        if (CollectionUtils.isEmpty(missionList)) {
            return;
        }
        //取任务
        Mission mission = null;
        for (Mission each : missionList) {
            if (each.isFinished() == false) {
                mission = each;
                break;
            }
        }
        //如果没拿到任务，说明全部任务列表已完成
        if (mission == null) {
            return;
        }
        //打开网页
        driver.get(mission.getPageUrl());
        listening = true;
        //点击播放按钮
        WebElement playButton = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div[2]/div[1]/div[1]/div[2]/button[1]"));
        playButton.click();
        playButton.click();
    }

    /**
     * 当收到websocket发来的url时的回调
     * 在websocket的子线程
     *
     * @param wsMessage
     */
    public synchronized static void onReceiveUrlCallback(WsMessage wsMessage) {
        if (listening == false) {
            return;
        }
        String eachUrl = wsMessage.getMessage();
        if (eachUrl.startsWith("https://audio.qingting.fm") == false) {
//        if (eachUrl.startsWith("https://od.sign.qingting.fm/") == false) {
            return;
        }
        //遍历任务列表，找到这个任务
        for (Mission mission : missionList) {
            String tabUrl = wsMessage.getTabUrl();
            String pageUrl = mission.getPageUrl();
            if (tabUrl.equals(pageUrl)) {
                if (mission.isFinished() == true) {
                    //结束
                    return;
                } else {
                    mission.setDownloadUrl(eachUrl);
                    //开始下载
                    System.out.println(mission.getName());
                    System.out.println(eachUrl);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //下载完成
                    listening = false;
                    //标记为已完成
                    mission.setFinished(true);
                    //下载下一个任务
                    driver.close();
                    driver.quit();
                    driver = null;
                    init();
                    downloadSingle();
                }
            }
        }

    }
}
