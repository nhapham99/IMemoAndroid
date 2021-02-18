package com.lnb.imemo.Model;

import java.util.List;

public class ResultTags {
    private List<Tags> tags;

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ResultTags{" +
                "tags=" + tags +
                '}';
    }
}
