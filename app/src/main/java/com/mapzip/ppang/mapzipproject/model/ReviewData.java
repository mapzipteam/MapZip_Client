package com.mapzip.ppang.mapzipproject.model;

import android.support.annotation.NonNull;

/**
 * Created by acekim on 16. 3. 3.
 */
public class ReviewData {
    @NonNull
    private String author;
    @NonNull
    private String comment;
    @NonNull
    private String date;

    public ReviewData(@NonNull String author, @NonNull String comment, @NonNull String date) {
        this.author = author;
        this.comment = comment;
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
    public String getComment() {
        return comment;
    }

    public void setComment(@NonNull String comment) {
        this.comment = comment;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }
}
