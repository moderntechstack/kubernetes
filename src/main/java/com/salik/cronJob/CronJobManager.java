package com.salik.cronJob;


import com.salik.config.Configurations;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CronJobManager {

    public void createCronJob(String appName) {
        CronJobUtils cronJobUtils = new CronJobUtils();
        HashMap<String, String> label = cronJobUtils.getLabels(appName);
        V1ObjectMeta podMetadata = cronJobUtils.getPodObjectMetadata(appName, label);
        ArrayList<V1EnvVar> containerEnvList = cronJobUtils.getEnvironmentVariables(appName);
        V1ResourceRequirements resources = cronJobUtils.getResources();
        String imageName = "mohdmsl/fibonacci:0.1";
        ArrayList<V1Container> container = cronJobUtils.getContainers(appName, imageName, containerEnvList, resources);
        V1PodSpec podSpec = cronJobUtils.getPodSpec(container);
        V1PodTemplateSpec templateSpec = cronJobUtils.getPodTemplateSpec(podMetadata, podSpec, label);
        V1JobSpec jobSpec = cronJobUtils.getJobSpec(1, templateSpec, label);
        V1ObjectMeta deploymentMetadata = cronJobUtils.getDeploymentPodMetadata(appName, label);
        V1JobTemplateSpec jobTemplateSpec = cronJobUtils.getJobTemplateSpec(podMetadata, jobSpec);
        V1CronJobSpec cronJobSpec = cronJobUtils.getCronJobSpec("*/2 * * * *", jobTemplateSpec);
        V1CronJob body = cronJobUtils.getCronJobBody(deploymentMetadata, cronJobSpec);


        try {
            ApiClient apiClient = new Configurations().getApiClientConf();
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            String namespace = "automation";
            apiInstance.createNamespacedCronJob(namespace, body, "true", null, null, null);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteCronJob() {
        V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
        v1DeleteOptions.setApiVersion("v1");
        v1DeleteOptions.setKind("DeleteOptions");
        v1DeleteOptions.setPropagationPolicy("Background");

        ApiClient apiClient = null;
        try {
            String appName = "cronjob-demo";
            String namespace = "automation";
            apiClient = new Configurations().getApiClientConf();
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            apiInstance.deleteNamespacedCronJob(appName, namespace, "true", null, null, false, "Background", v1DeleteOptions);
            System.out.println("Cron Job Deleted");
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }

    }
}
