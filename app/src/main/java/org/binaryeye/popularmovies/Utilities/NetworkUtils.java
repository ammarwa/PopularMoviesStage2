/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.binaryeye.popularmovies.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    final static String API_KEY = "api_key";
    final static String PAGE = "page";
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_VIDEOS =
            "https://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDEOS_REST =
            "/videos";
    private static final String MOVIE_REVIEWS_REST =
            "/reviews";
    /* The api key requested by TMDB */
    private static final String api_key = "6d77beda76db333f36b02f4a52c3d207";

    /**
     * Builds the URL used to talk to the TMDB server using a Api key.
     *
     * @return The URL to use to query the TMDB server.
     */
    public static URL buildUrl(int choice, int pageNumber, Long movieID) {
        Uri builtUri = null;
        if(choice == 0) {
            builtUri = Uri.parse(POPULAR_MOVIES_URL).buildUpon()
                    .appendQueryParameter(API_KEY, api_key)
                    .appendQueryParameter(PAGE, Integer.toString(pageNumber))
                    .build();
        } else if(choice == 1) {
            builtUri = Uri.parse(TOP_RATED_MOVIES_URL).buildUpon()
                    .appendQueryParameter(API_KEY, api_key)
                    .appendQueryParameter(PAGE, Integer.toString(pageNumber))
                    .build();
        } else if (choice == 2) {
            builtUri = Uri.parse(MOVIE_VIDEOS + movieID + MOVIE_VIDEOS_REST).buildUpon()
                    .appendQueryParameter(API_KEY, api_key)
                    .build();
        } else {
            builtUri = Uri.parse(MOVIE_VIDEOS + movieID + MOVIE_REVIEWS_REST).buildUpon()
                    .appendQueryParameter(API_KEY, api_key)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}