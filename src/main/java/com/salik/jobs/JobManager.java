package com.salik.jobs;


import com.salik.config.Configurations;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JobManager {

    public void createJob(String appName) {
        //String appName = "job-demo";
        JobUtils jobUtils = new JobUtils();
        HashMap<String, String> label = jobUtils.getLabels(appName);
        V1ObjectMeta podMetadata = jobUtils.getPodObjectMetadata(appName, label);
        ArrayList<V1EnvVar> containerEnvList = jobUtils.getEnvironmentVariables(appName);
        V1ResourceRequirements resources = jobUtils.getResources();
        String imageName = "mohdmsl/fibonacci:0.1";
        ArrayList<V1Container> container = jobUtils.getContainers(appName, imageName, containerEnvList, resources);
        V1PodSpec podSpec = jobUtils.getPodSpec(container);
        V1PodTemplateSpec templateSpec = jobUtils.getPodTemplateSpec(podMetadata, podSpec, label);
        V1JobSpec statefulSetSpec = jobUtils.getJobSpec(1, templateSpec, label);
        V1ObjectMeta deploymentMetadata = jobUtils.getDeploymentPodMetadata(appName, label);
        V1Job body = jobUtils.getJobBody(deploymentMetadata, statefulSetSpec);


        try {
            ApiClient apiClient = new Configurations().getApiClientConf();
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            String namespace = "automation";
            apiInstance.createNamespacedJob(namespace, body, "true", null, null, null);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteJob() {
        V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
        v1DeleteOptions.setApiVersion("v1");
        v1DeleteOptions.setKind("DeleteOptions");
        v1DeleteOptions.setPropagationPolicy("Background");

        ApiClient apiClient = null;
        try {
            String appName = "job-demo";
            String namespace = "automation";
            apiClient = new Configurations().getApiClientConf();
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            apiInstance.deleteNamespacedJob(appName, namespace, "true", null, null, false, "Background", v1DeleteOptions);
            System.out.println("Job Deleted");
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }

    }
}
