package com.salik.statefulSets;


import com.salik.config.Configurations;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class StatefulSetManager {

    public void createStatefulSet(String appName) {
        StatefulSetUtils statefulSetUtils = new StatefulSetUtils();
        HashMap<String, String> label = statefulSetUtils.getLabels(appName);
        V1ObjectMeta podMetadata = statefulSetUtils.getPodObjectMetadata(appName, label);
        ArrayList<V1EnvVar> containerEnvList = statefulSetUtils.getEnvironmentVariables(appName);
        V1ResourceRequirements resources = statefulSetUtils.getResources();
        String imageName = "mohdmsl/fibonacci:0.2";
        ArrayList<V1Container> container = statefulSetUtils.getContainers(appName, imageName, containerEnvList, resources);
        V1PodSpec podSpec = statefulSetUtils.getPodSpec(container);
        V1PodTemplateSpec templateSpec = statefulSetUtils.getPodTemplateSpec(podMetadata, podSpec, label);
        V1StatefulSetSpec statefulSetSpec = statefulSetUtils.getAppsStatefulSpecs(1, templateSpec, label);
        V1ObjectMeta deploymentMetadata = statefulSetUtils.getDeploymentPodMetadata(appName, label);
        V1StatefulSet body = statefulSetUtils.getAppsStatefulBody(deploymentMetadata, statefulSetSpec);


        try {
            ApiClient apiClient = new Configurations().getApiClientConf();
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            String namespace = "automation";
            apiInstance.createNamespacedStatefulSet(namespace, body, "true", null, null, null);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteStatefulSet() {
        V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
        v1DeleteOptions.setApiVersion("v1");
        v1DeleteOptions.setKind("DeleteOptions");
        v1DeleteOptions.setPropagationPolicy("Background");

        ApiClient apiClient = null;
        try {
            String appName = "statefulset-demo";
            String namespace = "automation";
            apiClient = new Configurations().getApiClientConf();
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            apiInstance.deleteNamespacedStatefulSet(appName, namespace, "true", null, null, false, "Background", v1DeleteOptions);
            System.out.println("StatefulSet Deleted");
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }

    }
}
