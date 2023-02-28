package com.ttran.gcp.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class GcpBucketService {

    public Storage getStorage(String projectId) {
        return StorageOptions.newBuilder()
                .setProjectId(projectId).build().getService();
    }


    public Bucket getBucket(String projectId, String bucketName) {
        final Storage storage = getStorage(projectId);
        Assert.notNull(storage, "Unable to get storage for project " + projectId);

        return storage.get(bucketName);
    }

    @SuppressWarnings("unchecked")
    public boolean createBlob(String blobName, Object content, String projectId, String bucketName) throws Exception {
        if (content instanceof List) {
            return createBlob(blobName, (List<String>) content, projectId, bucketName);
        } else if (content instanceof String) {
            return createBlob(blobName, (String) content, projectId, bucketName);
        }
        throw new UnsupportedOperationException("Content type has yet supported!");
    }

    public boolean createBlob(String blobName, String content, String projectId, String bucketName) {
        final Bucket bucket = getBucket(projectId, bucketName);
        Assert.notNull(bucket, String.format("Bucket not found, projectId: %s, bucketName: %s", projectId, bucketName));

        return createBlob(blobName, content, bucket);
    }

    public boolean createBlob(String blobName, String content, Bucket bucket) {
        final InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return bucket.create(blobName, contentStream) != null;
    }

    public boolean createBlob(String blobName, List<String> contents, String projectId, String bucketName) throws Exception {
        final Bucket bucket = getBucket(projectId, bucketName);
        Assert.notNull(bucket, String.format("Bucket not found, projectId: %s, bucketName: %s", projectId, bucketName));

        return createBlob(blobName, contents, bucket);
    }

    public boolean createBlob(String blobName, List<String> contents, Bucket bucket) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (String line : contents) {
            baos.write(line.getBytes(StandardCharsets.UTF_8));
        }

        return bucket.create(blobName, new ByteArrayInputStream(baos.toByteArray())) != null;
    }

    public String getBlobContent(String blobName, String projectId, String bucketName) throws Exception {
        final Bucket bucket = getBucket(projectId, bucketName);
        Assert.notNull(bucket, "Bucket not found. Project: " + projectId + ", bucketName: " + bucketName);
        final Blob downloadContent = bucket.get(blobName);
        return new String(downloadContent.getContent());
    }

}
