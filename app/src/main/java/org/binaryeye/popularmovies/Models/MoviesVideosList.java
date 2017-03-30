package org.binaryeye.popularmovies.Models;

/**
 * Created by ammar on 0030 30 Mar 17.
 */

public class MoviesVideosList {

    String id;
    MovieVideos [] results;

    public MoviesVideosList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovieVideos [] getResults() {
        return results;
    }

    public void setResults(MovieVideos [] results) {
        this.results = results;
    }
}
