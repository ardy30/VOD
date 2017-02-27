package com.ppfuns.model.entity;

import java.util.List;

/**
 * Created by zpf on 2016/8/16.
 */
public class PageContent<T> {

    /**
     * pageContent : [{"contentId":9650,"contentName":"0.5的爱情","contentPosters":"http://pic.pthv.gitv.tv/blueray/2014/11/20/14/46/05/1416465965381.jpg","contentType":1,"cornerMark":null,"cpId":16,"crEndDate":"2015-12-31 00:00:00.0","downTime":null,"expireDate":null,"focus":"一半冷心鸡汤 一半破碎灵魂","isAlbum":0,"isFree":0,"labels":"爱情","mainActors":"蒲巴甲 江语晨","operationTagName":null,"score":null,"seq":9650,"subNum":1}]
     * pageCount : 1
     * pageNo : 1
     * pageSize : 30
     * recCount : 1
     */

    public int pageCount;
    public int pageNo;
    public int pageSize;
    public Integer recCount;
    /**
     * contentId : 9650
     * contentName : 0.5的爱情
     * contentPosters : http://pic.pthv.gitv.tv/blueray/2014/11/20/14/46/05/1416465965381.jpg
     * contentType : 1
     * cornerMark : null
     * cpId : 16
     * crEndDate : 2015-12-31 00:00:00.0
     * downTime : null
     * expireDate : null
     * focus : 一半冷心鸡汤 一半破碎灵魂
     * isAlbum : 0
     * isFree : 0
     * labels : 爱情
     * mainActors : 蒲巴甲 江语晨
     * operationTagName : null
     * score : null
     * seq : 9650
     * subNum : 1
     */

    public T pageContent;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getRecCount() {
        return recCount;
    }

    public void setRecCount(int recCount) {
        this.recCount = recCount;
    }

    public T getPageContent() {
        return pageContent;
    }

    public void setPageContent(T pageContent) {
        this.pageContent = pageContent;
    }


}
