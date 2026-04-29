package ru.shatskikh.node.service.impl;


import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shatskikh.node.service.FileStoreService;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStoreServiceImpl implements FileStoreService {

    private final MinioClient minioClient;

    @Value("${minio.backetName}")
    private String bucketName;

    @Autowired
    public FileStoreServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String saveFile(InputStream inputStream, String originalFileName, long fileSize, String mimeType) {

       // String cleanName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        String folder = getFolderByMimeType(mimeType);

        String objectName = folder + "/" +UUID.randomUUID().toString() + "_" + originalFileName;

        try {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, fileSize, -1)
                            .contentType(mimeType)
                            .build()
            );

            return objectName;

        } catch (Exception ex) {

            throw new RuntimeException("Downloading on Minio Error", ex);
        }
    }


    private String getFolderByMimeType(String mimeType) {

        if(mimeType == null || !mimeType.contains("/")) {
            return "others";
        }

        String type = mimeType.split("/")[0];

        return switch (type) {
            case "application" -> "docs";
            case "image" -> "photos";
            case "video" -> "videos";
            case "audio" -> "audio";
            default -> type;
        };

    }


}
