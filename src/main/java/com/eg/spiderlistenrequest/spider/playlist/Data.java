/**
  * Copyright 2020 bejson.com 
  */
package com.eg.spiderlistenrequest.spider.playlist;
import java.util.List;

/**
 * Auto-generated: 2020-02-12 23:22:37
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data {

    private int total;
    private int curpage;
    private int pagesize;
    private List<Programs> programs;
    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setCurpage(int curpage) {
         this.curpage = curpage;
     }
     public int getCurpage() {
         return curpage;
     }

    public void setPagesize(int pagesize) {
         this.pagesize = pagesize;
     }
     public int getPagesize() {
         return pagesize;
     }

    public void setPrograms(List<Programs> programs) {
         this.programs = programs;
     }
     public List<Programs> getPrograms() {
         return programs;
     }

}