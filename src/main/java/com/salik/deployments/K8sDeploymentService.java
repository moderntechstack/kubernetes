package com.salik.deployments;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class K8sDeploymentService {


    /**
     * This method generates a HashMap of labels that can be used to identify and tag resources related
     * to a specific application in a Kubernetes environment.
     *
     * @param appName The name of the application for which the labels are being generated.
     * @return A HashMap containing the labels with key-value pairs as follows:
     */

    public HashMap<String, String> getLabels(String appName) {
        HashMap<String, String> labels = new HashMap<>();
        labels.put("uuid", UUID.randomUUID().toString());
        labels.put("app", appName);
        labels.put("timestamp", String.valueOf(ZonedDateTime.now().toEpochSecond()));
        return labels;
    }

    public V1ObjectMeta getPodObjectMetadata(String appName, HashMap<String, String> labels) {
        V1ObjectMeta podObjectMetaData = new V1ObjectMeta();
        podObjectMetaData.setName(appName);
        podObjectMetaData.setLabels(labels);
        return podObjectMetaData;
    }

    /**
     * creates a environment variable object for k8s
     *
     * @param appName
     * @return
     */
    public ArrayList<V1EnvVar> getEnvironmentVariables(String appName) {
        ArrayList<V1EnvVar> envList = new ArrayList<V1EnvVar>();
        V1EnvVar env = new V1EnvVar();
        env.setName("appName");
        env.value(appName);
        envList.add(env);
        return envList;
    }


    /**
     * This method creates and configures a list of Kubernetes V1Container objects for a pod. Each
     * V1Container represents a container running within the pod.
     *
     * @param containerName    The name to be assigned to the container.
     * @param containerImage   The container image to be used for the container.
     * @param containerEnvList A list of environment variables to be set for the container.
     * @param resources        The resource requirements (CPU and memory) for the container.
     * @return An ArrayList of V1Container objects containing the configured container details.
     */
    public ArrayList<V1Container> getContainers(String containerName, String containerImage, ArrayList<V1EnvVar> containerEnvList,
                                                V1ResourceRequirements resources) {
        ArrayList<V1Container> containerList = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName(containerName);
        container.setImage(containerImage);
        container.setEnv(containerEnvList);
        container.setImagePullPolicy("IfNotPresent");
        container.setResources(resources);
        containerList.add(container);
        return containerList;
    }

    /**
     * This method returns a Kubernetes V1ResourceRequirements object that defines resource limits and
     * resource requests for a Kubernetes Pod. The method sets CPU and memory limits and requests for
     * the Pod. The limits define the maximum amount of CPU and memory that the Pod can use, while the
     * requests define the minimum amount of CPU and memory that the Pod needs to be scheduled onto a
     * node.
     *
     * @return
     */
    public V1ResourceRequirements getResources() {
        V1ResourceRequirements resource = new V1ResourceRequirements();
        resource.putLimitsItem("cpu", Quantity.fromString("200m"));
        resource.putLimitsItem("memory", Quantity.fromString("500Mi"));
        resource.putRequestsItem("cpu", Quantity.fromString("100m"));
        resource.putRequestsItem("memory", Quantity.fromString("100Mi"));
        return resource;
    }

    public V1PodSpec getPodSpec(ArrayList<V1Container> containers) {
        V1PodSpec podspec = new V1PodSpec();
        podspec.setContainers(containers);
        podspec.setRestartPolicy("Always");
        return podspec;
    }

    public V1PodTemplateSpec getPodTemplateSpec(V1ObjectMeta objectMetadata, V1PodSpec podSpec, HashMap<String, String> labels) {
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
        objectMetadata.setLabels(labels);
        podTemplateSpec.setMetadata(objectMetadata);
        podTemplateSpec.setSpec(podSpec);
        return podTemplateSpec;
    }

    public V1DeploymentSpec getAppsDeploymentSpecs(Integer replicas, V1PodTemplateSpec podTemplateSpec, HashMap<String, String> labels) {
        V1LabelSelector lSpec = new V1LabelSelector();
        V1DeploymentSpec spec = new V1DeploymentSpec();
        lSpec.setMatchLabels(labels);
        spec.setSelector(lSpec);
        spec.setReplicas(replicas);
        spec.setTemplate(podTemplateSpec);
        return spec;
    }

    public V1ObjectMeta getDeploymentPodMetadata(String appName, HashMap<String, String> labels) {
        V1ObjectMeta podObjectMetaData = new V1ObjectMeta();
        podObjectMetaData.setName(appName);
        podObjectMetaData.setLabels(labels);
        return podObjectMetaData;
    }

    public V1Deployment getAppsDeploymentBody(V1ObjectMeta deploymentObjectMetaData, V1DeploymentSpec deploymentSpec) {
        V1Deployment body = new V1Deployment();
        body.setKind("Deployment");
        body.setApiVersion("apps/v1");
        body.setMetadata(deploymentObjectMetaData);
        body.setSpec(deploymentSpec);
        return body;
    }

}
