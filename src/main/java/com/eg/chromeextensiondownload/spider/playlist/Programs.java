/**
  * Copyright 2020 bejson.com 
  */
package com.eg.chromeextensiondownload.spider.playlist;
import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2020-02-12 23:22:37
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Programs {

    private long id;
    private String title;
    private int duration;
    private Date update_time;
    private int sequence;
    private List<Long> file_size;
    private String playcount;
    public void setId(long id) {
         this.id = id;
     }
     public long getId() {
         return id;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setDuration(int duration) {
         this.duration = duration;
     }
     public int getDuration() {
         return duration;
     }

    public void setUpdate_time(Date update_time) {
         this.update_time = update_time;
     }
     public Date getUpdate_time() {
         return update_time;
     }

    public void setSequence(int sequence) {
         this.sequence = sequence;
     }
     public int getSequence() {
         return sequence;
     }

    public void setFile_size(List<Long> file_size) {
         this.file_size = file_size;
     }
     public List<Long> getFile_size() {
         return file_size;
     }

    public void setPlaycount(String playcount) {
         this.playcount = playcount;
     }
     public String getPlaycount() {
         return playcount;
     }

}