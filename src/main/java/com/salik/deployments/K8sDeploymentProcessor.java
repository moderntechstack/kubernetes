package com.salik.deployments;


import com.salik.config.Configurations;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class K8sDeploymentProcessor {

    public void createDeployment(String appName) {

        K8sDeploymentService k8sDeploymentService = new K8sDeploymentService();
        HashMap<String, String> label = k8sDeploymentService.getLabels(appName);
        V1ObjectMeta podMetadata = k8sDeploymentService.getPodObjectMetadata(appName, label);
        ArrayList<V1EnvVar> containerEnvList = k8sDeploymentService.getEnvironmentVariables(appName);
        V1ResourceRequirements resources = k8sDeploymentService.getResources();
        String imageName = "mohdmsl/fibonacci:0.2";
        ArrayList<V1Container> container = k8sDeploymentService.getContainers(appName, imageName, containerEnvList, resources);
        V1PodSpec podSpec = k8sDeploymentService.getPodSpec(container);
        V1PodTemplateSpec templateSpec = k8sDeploymentService.getPodTemplateSpec(podMetadata, podSpec, label);
        V1DeploymentSpec deploymentSpec = k8sDeploymentService.getAppsDeploymentSpecs(3, templateSpec, label);
        V1ObjectMeta deploymentMetadata = k8sDeploymentService.getDeploymentPodMetadata(appName, label);
        V1Deployment body = k8sDeploymentService.getAppsDeploymentBody(deploymentMetadata, deploymentSpec);


        try {
            ApiClient apiClient = new Configurations().getApiClientConf();
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            String namespace = "automation";
            apiInstance.createNamespacedDeployment(namespace, body, "true", null, null, null);
            System.out.println("Deployment Created");

        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteDeployment(String appName) {
        V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
        v1DeleteOptions.setApiVersion("v1");
        v1DeleteOptions.setKind("DeleteOptions");
        v1DeleteOptions.setPropagationPolicy("Background");

        ApiClient apiClient = null;
        try {
            String namespace = "automation";
            apiClient = new Configurations().getApiClientConf();
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            apiInstance.deleteNamespacedDeployment(appName, namespace, "true", null, null, false, "Background", v1DeleteOptions);
            System.out.println("Deployment Deleted");
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }

    }
}
