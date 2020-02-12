package com.eg.spiderlistenrequest.spider;

import com.eg.spiderlistenrequest.ws.WsUtil;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.sound.midi.Soundbank;
import java.io.File;
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

    /**
     * 开始运行爬虫
     */
    public static void run() {
        //等待插件就绪
        waitForExtensionReady();
        System.out.println("extension ready");
        //插件已就绪，可以开始表演了
        driver.get("https://www.qingting.fm/channels/237072/programs/8397875");
        WebElement list = driver.findElement(
                By.xpath("//*[@id=\"app\"]/div/div[2]/div[2]/div[1]/div[2]/div/ul"));
        List<WebElement> elements = list.findElements(By.className("program-row-root"));
        for (WebElement li : elements) {
            WebElement button = li.findElement(By.className("col1"));
            button.click();
        }
    }


}
