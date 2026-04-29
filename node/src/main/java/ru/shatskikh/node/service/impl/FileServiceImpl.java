package ru.shatskikh.node.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shatskikh.entity.AppDocument;
import ru.shatskikh.entity.enums.FileType;
import ru.shatskikh.node.service.FileService;
import ru.shatskikh.node.service.FileStoreService;
import ru.shatskikh.node.exceptions.UploadFileException;
import ru.shatskikh.repository.AppDocumentRepository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final FileStoreService fileStoreService;
    private final AppDocumentRepository appDocumentRepository;


    @Autowired
    public FileServiceImpl(FileStoreService fileStoreService, AppDocumentRepository appDocumentRepository) {
        this.fileStoreService = fileStoreService;
        this.appDocumentRepository = appDocumentRepository;
    }

    @Override
    @Transactional
    public AppDocument processFile(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONObject result = jsonObject.getJSONObject("result");

            String filePath = String.valueOf(result.getString("file_path"));
            long fileSize = result.getLong("file_size");

            Document telegramDoc = telegramMessage.getDocument();

            try (InputStream is = downloadFile(filePath)) {

                String fileKey = fileStoreService.saveFile(
                        is,
                        telegramDoc.getFileName(),
                        fileSize,
                        telegramDoc.getMimeType()
                );

                FileType fileType = FileType.determineType(telegramDoc.getMimeType());

                AppDocument transientAppDoc = AppDocument.builder()
                        .telegramFileId(fileId)
                        .docName(telegramDoc.getFileName())
                        .mimeType(telegramDoc.getMimeType())
                        .fileSize(fileSize)
                        .fileType(fileType)
                        .s3Key(fileKey)
                        .build();

                return appDocumentRepository.save(transientAppDoc);

            } catch (Exception ex) {
                throw new UploadFileException("File processing error", ex);
            }
        }
        throw new UploadFileException("Bad response from telegram");
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(fileInfoUri, HttpMethod.GET, request, String.class, token, fileId);

    }

    private InputStream downloadFile(String filePath) {
        String fullUri = fileStorageUri
                .replace("{token}", token)
                .replace("{filePath}", filePath);

        try {
           URL urlObj = new URL(fullUri);
           return new BufferedInputStream(urlObj.openStream());
        } catch (IOException ex) {
            throw new UploadFileException("Error download URL: ", ex);
        }

    }

}
