package com.lnb.imemo.Model;

public class Pagination{
    private int totalItems;
    private boolean hasMore;

    public Pagination(int totalItems, boolean hasMore) {
        this.totalItems = totalItems;
        this.hasMore = hasMore;
    }

    public Pagination() {
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "totalItems=" + totalItems +
                ", hasMore=" + hasMore +
                '}';
    }
}