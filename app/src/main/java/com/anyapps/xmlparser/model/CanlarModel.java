package com.anyapps.xmlparser.model;

/**
 * Created by alawn.xu on 2017/8/22.
 */

public class CanlarModel extends BaseModel {
    private String lastest;
    private String lastestDate;
    private String year;
    private String date;
    private String uri;

    public String getLastest() {
        return lastest;
    }

    public void setLastest(String lastest) {
        this.lastest = lastest;
    }

    public String getLastestDate() {
        return lastestDate;
    }

    public CanlarModel setLastestDate(String lastestDate) {
        this.lastestDate = lastestDate;
        return this;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "lastest:".concat(getLastest())
                .concat("lastestDate:".concat(getLastestDate()))
                .concat("year:".concat(getYear()))
                .concat("date:".concat(getDate()))
                .concat("uri:".concat(getUri()));
    }
}
