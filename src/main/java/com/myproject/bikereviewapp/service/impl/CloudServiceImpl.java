package com.myproject.bikereviewapp.service.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.myproject.bikereviewapp.entity.pojo.FileUploadPojo;
import com.myproject.bikereviewapp.exceptionhandler.exception.CloudException;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import com.myproject.bikereviewapp.validation.annotation.NotEmptyMultipartFile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;


@Service
@Validated
public class CloudServiceImpl implements CloudService {

    @Value("${cache.control.dynamic.max-age}")
    private int cacheControlMaxAge;


    @Value("${firebase.access.file-name}")
    private String firebaseAccessFileName;

    @Value("${firebase.bucket-name}")
    private String bucketName;


    @Override
    public FileUploadPojo upload(@Valid @NotEmptyMultipartFile MultipartFile multipartFile, String folderName) {

        try {
            String fileExtension = getExtension(multipartFile.getOriginalFilename());

            String completeFileName = UUID.randomUUID() + fileExtension;
            File file = convertToFile(multipartFile, completeFileName);

            String url = uploadFile(file, completeFileName, folderName);
            file.delete();

            return new FileUploadPojo(completeFileName, url);

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

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").setCacheControl(getCacheControlHeader()).build();

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


    private void updateCacheControlForAllFiles() throws IOException {
        Storage storage = initializeStorage();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list().iterateAll()) {
            BlobInfo updatedBlobInfo = blob.toBuilder()
                    .setCacheControl(getCacheControlHeader())
                    .build();

            storage.update(updatedBlobInfo);
            System.out.println("Updated Cache Control for: " + blob.getName());
        }
    }

    private String getCacheControlHeader() {
        return "max-age=" + cacheControlMaxAge;
    }
}

