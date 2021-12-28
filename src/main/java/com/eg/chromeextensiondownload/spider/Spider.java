package com.eg.chromeextensiondownload.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eg.chromeextensiondownload.spider.playlist.Data;
import com.eg.chromeextensiondownload.spider.playlist.ListRespone;
import com.eg.chromeextensiondownload.spider.playlist.Programs;
import com.eg.chromeextensiondownload.util.Constants;
import com.eg.chromeextensiondownload.util.HttpUtil;
import com.eg.chromeextensiondownload.ws.WsMessage;
import com.eg.chromeextensiondownload.ws.WsUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        String channelJson = HttpUtil.get("https://i.qingting.fm/capi/v3/channel/"
                + Constants.CHANNEL_ID + "?user_id=null");
        JSONObject channelData = JSONObject.parseObject(channelJson).getJSONObject("data");
        String v = channelData.getString("v");
        Constants.CHANNEL_TITLE = channelData.getString("title");
        int curpage = 1;
        int total;
        do {
            String listJson = HttpUtil.get("https://i.qingting.fm/capi/channel/" + Constants.CHANNEL_ID
                    + "/programs/" + v + "?curpage=" + curpage + "&pagesize=30&order=asc");
            curpage++;
            ListRespone listRespone = JSON.parseObject(listJson, ListRespone.class);
            Data data = listRespone.getData();
            total = data.getTotal();
            List<Programs> programs = data.getPrograms();
            for (Programs program : programs) {
                int index = program.getSequence() + 1;
                String zero = index + "";
                if (index <= 9) {
                    zero = "0" + index;
                }
                String filename = zero + program.getTitle();
                String pageUrl = "https://www.qingting.fm/channels/" + Constants.CHANNEL_ID + "/programs/"
                        + program.getId() + "/";
                Mission mission = new Mission();
                mission.setName(filename);
                mission.setPageUrl(pageUrl);
                missionList.add(mission);
            }
        } while (CollectionUtils.isNotEmpty(missionList) && missionList.size() < total);
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
        //这块可能会出现广告，先刷新一下
        driver.navigate().refresh();
        listening = true;
        //点击播放按钮
        WebElement playButton = driver.findElement(By.xpath(
                "//*[@id=\"app\"]/div/div[3]/div[2]/div[1]/div[1]/div[2]/button[1]"));
        playButton.click();
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
        System.out.println(eachUrl);
//        if (eachUrl.startsWith("https://audio.qingting.fm") == false) {
        if (eachUrl.startsWith("https://od.sign.qingting.fm/") == false) {
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
                    File folder = new File(Constants.DOWNLOAD_LOCATION
                            + File.separator + Constants.CHANNEL_TITLE);
                    if (folder.exists() == false) {
                        folder.mkdirs();
                    }
                    File file = new File(folder, mission.getName() + "." + mission.getFormat());
                    try {
                        FileUtils.copyURLToFile(new URL(mission.getDownloadUrl()), file);
                    } catch (IOException e) {
                        try {
                            FileUtils.copyURLToFile(new URL(mission.getDownloadUrl()), file);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        e.printStackTrace();
                    }
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
