package org.binaryeye.popularmovies.Models;

/**
 * Created by ammar on 0030 30 Mar 17.
 */

public class MovieReviewsList {
    String id;
    String page;
    MovieReviews[] results;
    String total_pages;
    String total_results;

    public MovieReviewsList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public MovieReviews[] getResults() {
        return results;
    }

    public void setResults(MovieReviews[] results) {
        this.results = results;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }

    public String getTotal_results() {
        return total_results;
    }

    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }
}
