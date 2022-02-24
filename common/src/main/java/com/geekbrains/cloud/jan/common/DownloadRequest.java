package com.geekbrains.cloud.jan.common;

public class DownloadRequest extends AbstractMessage {   // запрос на заргузку
    private String filename;

    public String getFilename() {
        return filename;
    }

    public DownloadRequest(String filename) {
        this.filename = filename;
    }
}
