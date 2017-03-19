package org.binaryeye.popularmoviesstage1.Models;

/**
 * Created by ammar on 0019 19 Mar 17.
 */

public class TMDBJsonResponse {
    int page;
    Result [] results;
    String total_results;
    String total_pages;

    public TMDBJsonResponse() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
    }

    public String getTotal_results() {
        return total_results;
    }

    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }
}
