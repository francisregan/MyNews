package com.reader.news.reganfrancis.models;

import com.reader.news.reganfrancis.utils.Misc;
import java.io.Serializable;
/**
 * Created by reganfrancis on 28/11/14.
 */
public class NewsItem implements Serializable{

    public String _id;
    public String headlines;
    public String redirect_url;
    public String category;
    public String description;
    public String time;
    public String imageurl;
    public String article_id;
    public String created_at;
    public int count = 40;

    public NewsItem(String title, String description, String website)
    {
        this.headlines = title;
        this.description = description;
        this.redirect_url = CheckURL(website);
    }

    public String CheckURL(String website)
    {
        if(website.startsWith("http://")) return website;
        return Misc.URLofTOI + website;
    }

}
