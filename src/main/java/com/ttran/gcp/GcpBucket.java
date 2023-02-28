package com.ttran.gcp;

import com.google.cloud.ReadChannel;
import com.google.cloud.bigquery.migration.v2.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.Timestamp;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GcpBucket {

    public static void main(String[] args) throws Exception {
//        Credentials credentials = GoogleCredentials
//                .fromStream(new FileInputStream("/Users/taotran/Downloads/root-stock-378622-6f53de22b4af.json"));

        final String projectId = "root-stock-378622";
        final String inPath = "gs://thom_test_bucket/test_hive_copy.hql";
        final String outPath = "gs://thomastran-bucket";
        final String workflowName = "testWf";
        final String taskName = "testTask";
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
//                .setProjectId(projectId).build().getService();

        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId).build().getService();
//        Bucket bucket = storage.create(BucketInfo.of("thomastran-bucket"));
        Bucket bucket = storage.get("thomastran-bucket");

        String blobName = "my_blob_name";
        InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
        Blob blob = bucket.create(blobName, content);

        Blob downloadContent = bucket.get(blobName);



//        ReadChannel readChannel = downloadContent.reader();
//        FileOutputStream fileOutputStream = new FileOutputStream("testDownloadFromGCPBucket");
//        fileOutputStream.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
//        PrintStream printStream = new PrintStream(fileOutputStream);
//        byte[] bytes = new byte[]{};
//        printStream.write(bytes);
//        final String downloadContentString = new String(bytes);


        System.out.println("DATAAAA " + new String(blob.getContent()));


//        fileOutputStream.close();


        System.out.println("downloadContent = " + downloadContent);


        MigrationServiceSettings migrationServiceSettings = MigrationServiceSettings.newBuilder().build();
        MigrationServiceClient client = MigrationServiceClient.create(migrationServiceSettings);

        LocationName parent = LocationName.of("root-stock-378622", "us");

        MigrationWorkflow migrationWorkflow = createTranslationWorkflow(client, projectId, parent, outPath, inPath,
                workflowName, taskName);

        MigrationWorkflow response = client.createMigrationWorkflow(parent, migrationWorkflow);

        reportWorkflowStatus(response);

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
                    .setGcsSourcePath(inPath)
                    .setGcsTargetPath(outPath)
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


}
