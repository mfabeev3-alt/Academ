package ru.shatskikh.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shatskikh.repository.AppDocumentRepository;

import java.util.concurrent.TimeUnit;

@Service
public class FileService {

    private final MinioClient minioClient;
    private final AppDocumentRepository appDocumentRepository;

    @Autowired
    public FileService(MinioClient minioClient, AppDocumentRepository appDocumentRepository) {
        this.minioClient = minioClient;
        this.appDocumentRepository = appDocumentRepository;
    }

    @Value("${minio.backetName}")
    private String bucket;



}
