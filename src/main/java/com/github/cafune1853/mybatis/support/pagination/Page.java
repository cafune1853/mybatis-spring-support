package com.github.cafune1853.mybatis.support.pagination;

public class Page {
    /**
     * 起始编号为1
     */
    private final int pageNo;

    private final int pageSize;

    private boolean countTotal;

    private int totalNumber;

    public Page(int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("PageNumber must >= 1");
        }
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Page(int pageNo, int pageSize, boolean countTotal) {
        this(pageNo, pageSize);
        this.setCountTotal(countTotal);
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isCountTotal() {
        return countTotal;
    }

    public Page setCountTotal(boolean countTotal) {
        this.countTotal = countTotal;
        return this;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public Page setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
        return this;
    }
}
