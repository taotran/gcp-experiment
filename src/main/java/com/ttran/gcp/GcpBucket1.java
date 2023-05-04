package com.ttran.gcp;

import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.migration.v2.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.Timestamp;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GcpBucket1 {

    public static void main(String[] args) {
        final String projectId = "root-stock-378622";
        final Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//        final Storage storage1 = StorageOptions.newBuilder().setProjectId("root-stock-378622").build().getService();
//        final Bucket bucket1 = storage.get("axp-lumid-mdm2/temp/hive_bq_conversion/500987a7-ec25-4b72-a2fb-d0661cbc99b3");
        final Bucket bucket1 = storage.get("axp-lumid-mdm2");
        final Page<Blob> blobs = bucket1.list(Storage.BlobListOption.prefix("temp/hive_bq_conversion/500987a7-ec25-4b72-a2fb-d0661cbc99b3"));

        System.out.println(blobs);


    }

    public static void main1(String[] args) throws Exception {
//        Credentials credentials = GoogleCredentials
//                .fromStream(new FileInputStream("/Users/taotran/Downloads/root-stock-378622-6f53de22b4af.json"));
        final UUID mockAppFlowId;
        mockAppFlowId = UUID.randomUUID();
        final UUID mockWorkflowId = UUID.randomUUID();
        final UUID operatorId = UUID.randomUUID();
//        final String projectId = "axp-lumid-379921";
        final String projectId = "root-stock-378622";
        final String inPath = "axp-lumid-mdm2";
        final String outPath = "axp-lumid-mdm2";
        final String srcFilePath = "temp/hive_bq_conversion_src/default.hql";
        final String destFilePath = "temp/hive_bq_conversion/default.hql";
        final String workflowName = "testWf" + 1;
        final String taskName = "testTask" + 1;
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
//                .setProjectId(projectId).build().getService();

//        Storage storage = StorageOptions.newBuilder()
//                .setProjectId(projectId).build().getService();
////        Bucket bucket = storage.create(BucketInfo.of("thomastran-bucket"));
//        Bucket bucket = storage.get("thomastran-bucket");
//
//        String blobName = "my_blob_name";
//        InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
//        Blob blob = bucket.create(blobName, content);
//
//        Blob downloadContent = bucket.get(blobName);


//        ReadChannel readChannel = downloadContent.reader();
//        FileOutputStream fileOutputStream = new FileOutputStream("testDownloadFromGCPBucket");
//        fileOutputStream.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
//        PrintStream printStream = new PrintStream(fileOutputStream);
//        byte[] bytes = new byte[]{};
//        printStream.write(bytes);
//        final String downloadContentString = new String(bytes);


//        System.out.println("DATAAAA " + new String(blob.getContent()));


//        fileOutputStream.close();


//        System.out.println("downloadContent = " + downloadContent);

        final String constructedSrcFilePath = String.format("%s/%s/%s_%s.hql",
                extractFilePathFromFullPath(srcFilePath), mockAppFlowId,
                mockWorkflowId, operatorId);

        final Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//        final Storage storage1 = StorageOptions.newBuilder().setProjectId("root-stock-378622").build().getService();
        final Bucket bucket1 = storage.get("axp-lumid-mdm2");
//        final Bucket bucket = storage.get(constructedSrcFilePath);

        boolean created = createBlob(constructedSrcFilePath, Collections.singletonList("SELECT * FROM abc"), bucket1);

        System.out.println("created = " + created);

        MigrationServiceSettings migrationServiceSettings = MigrationServiceSettings.newBuilder().build();
        MigrationServiceClient client = MigrationServiceClient.create(migrationServiceSettings);

        LocationName parent = LocationName.of(projectId, "us");

        final String wfSrcFilePath = String.format("%s/%s/%s", inPath, extractFilePathFromFullPath(srcFilePath),
                mockAppFlowId);

        final String wfDestFilePath = String.format("%s/%s/%s", outPath, extractFilePathFromFullPath(destFilePath),
                mockAppFlowId);

        MigrationWorkflow migrationWorkflow = createTranslationWorkflow(client, projectId, parent, wfDestFilePath, wfSrcFilePath,
                workflowName, taskName);

        MigrationWorkflow response = client.createMigrationWorkflow(parent, migrationWorkflow);

        reportWorkflowStatus(response);

    }

    private static boolean createBlob(String constructedSrcFilePath, List<String> singletonList, Bucket bucket) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (String s : singletonList) {
            baos.write(s.getBytes(StandardCharsets.UTF_8));
        }
        return bucket.create(constructedSrcFilePath, new ByteArrayInputStream(baos.toByteArray())) != null;
    }

    private static Object extractFilePathFromFullPath(String fullFilePath) {
        if (!fullFilePath.contains(".hql")) {
            return fullFilePath;
        }

        final int lastIndex = fullFilePath.lastIndexOf("/");
        return fullFilePath.substring(0, lastIndex);
    }

    private static void reportWorkflowStatus(MigrationWorkflow workflow) {
        System.out.println("Migration workflow " + workflow.getName() + " is in state " + workflow.getState() + ".");

        for (Map.Entry<String, MigrationTask> entry : workflow.getTasksMap().entrySet()) {
            String k = entry.getKey();
            MigrationTask task = entry.getValue();

            System.out.println(" - Task " + k + " had id " + task.getId());

            if (task.hasProcessingError()) {
                System.out.println(" with processing error: " + task.getProcessingError().getReason());
            }
            System.out.println();
        }
    }

    private static MigrationWorkflow createTranslationWorkflow(MigrationServiceClient client, String projectId, LocationName parent, String outPath, String inPath, String workflowName, String taskName) {
        MigrationWorkflow workflow = null;
        try {
            TranslationConfigDetails taskDetails = TranslationConfigDetails.newBuilder()
                    .setGcsSourcePath("gs://" + inPath)
                    .setGcsTargetPath("gs://" + outPath)
                    .setSourceDialect(Dialect.newBuilder().setHiveqlDialect(HiveQLDialect.newBuilder().build()).build())
                    .setTargetDialect(Dialect.newBuilder().setBigqueryDialect(BigQueryDialect.newBuilder().build()).build())
                    .build();

            MigrationTask task = MigrationTask.newBuilder()
                    .setType("Translation_HiveQL2BQ")
                    .setTranslationConfigDetails(taskDetails)
                    .build();

            workflow = MigrationWorkflow.newBuilder()
                    .setName(MigrationWorkflowName.of(projectId, parent.getLocation(), workflowName).toString())
                    .setDisplayName(workflowName)
                    .putTasks(taskName, task)
                    .setCreateTime(Timestamp.newBuilder().build())
                    .setLastUpdateTime(Timestamp.newBuilder().build())
                    .build();
            System.out.println("migration workflow defined.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflow;
    }

    private void createSrcFiles(String inPath) {

    }


}
