package com.myproject.bikereviewapp.service.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.myproject.bikereviewapp.exceptionhandler.exception.CloudException;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


@Service
public class CloudServiceImpl implements CloudService {

    @Value("${firebase.access.file-name}")
    private String firebaseAccessFileName;
    
    @Value("${firebase.bucket-name}")
    private String bucketName;


    @Override
    public String upload(MultipartFile multipartFile, String folderName, String fileName) {

        try {
            String fileExtension = getExtension(multipartFile.getOriginalFilename());
            String completeFileName = fileName + fileExtension;
            File file = convertToFile(multipartFile, completeFileName);
            String url = uploadFile(file, completeFileName, folderName);
            file.delete();
            return url;

        } catch (IOException e) {
            throw new CloudException("Error when uploading file.");
        }
    }

    @Override
    public void delete(String folderName, String fileName) {
        try {
            String blobName = folderName + "/" + fileName;
            BlobId blobId = BlobId.of(bucketName, blobName);
            Storage storage = initializeStorage();
            storage.delete(blobId);
        } catch (IOException e) {
            throw new CloudException("Error when deleting file.");
        }
    }

    private String uploadFile(File file, String fileName, String folderName) throws IOException {

        String blobName = folderName + "/" + fileName;
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

        Storage storage = initializeStorage();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String downloadUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/%s?alt=media";
        return String.format(downloadUrl, URLEncoder.encode(blobName, StandardCharsets.UTF_8));
    }

    private Storage initializeStorage() throws IOException {

        try (InputStream inputStream = CloudServiceImpl.class.getClassLoader().getResourceAsStream(firebaseAccessFileName)) {
            Credentials credentials = GoogleCredentials.fromStream(inputStream);
            return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        }

    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {

        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }
}

