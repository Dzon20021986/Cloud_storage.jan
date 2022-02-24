package com.geekbrains.cloud.jan.common;

public class DeleteRequest extends AbstractMessage {  // запрос на удаление

    private String filename;

    public String getFilename() {
        return filename;
    }

    public DeleteRequest(String filename) {
        this.filename = filename;
    }
}
