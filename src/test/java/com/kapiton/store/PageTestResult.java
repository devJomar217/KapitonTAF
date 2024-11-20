package com.kapiton.store;

import java.util.ArrayList;
import java.util.List;

public class PageTestResult {
    private String pageName;
    private List<PageLoadData> pageLoadDataList = new ArrayList<>();

    public PageTestResult(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }

    public List<PageLoadData> getPageLoadDataList() {
        return pageLoadDataList;
    }

    public void addPageLoadData(int userID, long loadTime) {
        pageLoadDataList.add(new PageLoadData(userID, loadTime));
    }

    @Override
    public String toString() {
        return "UserTestResult{" +
                "pageName='" + pageName + '\'' +
                ", pageLoadTimes=" + pageLoadDataList +
                '}';
    }
}

class PageLoadData {
    private int userID;
    private long loadTime;

    public PageLoadData(int userID, long loadTime) {
        this.userID = userID;
        this.loadTime = loadTime;
    }

    public int getUserID() {
        return userID;
    }

    public long getLoadTime() {
        return loadTime;
    }
}
