package com.reader.news.reganfrancis.models;

import java.io.Serializable;

/**
 * Created by reganfrancis on 2/12/14.
 */
public class NewsDetailItem implements Serializable {

    public String _id;
    public String headlines;
    public String full_description;
    public String category;
    public String coverImage = "none";
    public String[] image;
    public String redirect_url;
    public String created_at;

}
