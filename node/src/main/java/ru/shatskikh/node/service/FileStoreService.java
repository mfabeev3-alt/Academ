package ru.shatskikh.node.service;

import java.io.InputStream;

public interface FileStoreService {
    public String saveFile(InputStream inputStream, String originalFileName, long fileSize, String contentType);
}
