package com.anglefinance;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.JobWorker;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Camunda8RestConnectorTest {
    private static final String ZEEBE_GATEWAY_ADDRESS = "localhost:26500"; // Zeebe Gateway address
    private static final String CLIENT_ID = "your-client-id"; // Client ID for authentication
    private static final String CLIENT_SECRET = "your-client-secret"; // Client Secret for authentication
    private static final String AUTH_URL = "your-auth-server-url"; // Authorization server URL

    @Test
    public void testJobWorker() {

        //Setup for Saas Camunda cluster.....

//        try (ZeebeClient client = ZeebeClient.newCloudClientBuilder()
//                .withClusterId("your-cluster-id") // Replace with your cluster ID
//                .withClientId(CLIENT_ID)
//                .withClientSecret(CLIENT_SECRET)
//                .withAuthServer(AUTH_URL)
//                .build()) {

        //using local docker instance - configured in docker-compose.yml

        try (ZeebeClient client = ZeebeClient.newClientBuilder()
                .gatewayAddress("127.0.0.1:26500")
                .usePlaintext()
                .build()) {

            // Start a job worker
            JobWorker worker = client.newWorker()
                    .jobType("example-job")
                    .handler((jobClient, job) -> {
                        System.out.println("Processing job: " + job);
                        // Complete the job
                        jobClient.newCompleteCommand(job.getKey()).send().join();
                    })
                    .open();

            // Fetch and assert job handling
            ActivatedJob activatedJob = client.newActivateJobsCommand()
                    .jobType("example-job")
                    .maxJobsToActivate(1)
                    .send()
                    .join()
                    .getJobs()
                    .get(0);

            assertNotNull(activatedJob, "Job should not be null");
            System.out.println("Activated Job: " + activatedJob);
        }
    }
}
