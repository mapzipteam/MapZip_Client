package com.mapzip.ppang.mapzipproject.model;

import android.support.annotation.NonNull;

/**
 * Created by acekim on 16. 3. 3.
 */
public class ReviewData {
    @NonNull
    private String author;
    @NonNull
    private String review;
    @NonNull
    private String date;

    public ReviewData(@NonNull String author, @NonNull String review, @NonNull String date) {
        this.author = author;
        this.review = review;
        this.date = date;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
        this.author = author;
    }

    @NonNull
    public String getReview() {
        return review;
    }

    public void setReview(@NonNull String review) {
        this.review = review;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }
}
