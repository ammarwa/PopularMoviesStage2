package org.binaryeye.popularmovies.Models;

/**
 * Created by ammar on 0030 30 Mar 17.
 */

public class MovieReviews {

    String id;
    String author;
    String content;
    String url;

    public MovieReviews() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
