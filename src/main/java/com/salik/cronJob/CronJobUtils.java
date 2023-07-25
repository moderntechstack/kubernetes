package com.salik.cronJob;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CronJobUtils {

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

    public ArrayList<V1EnvVar> getEnvironmentVariables(String appName) {
        ArrayList<V1EnvVar> envList = new ArrayList<V1EnvVar>();
        V1EnvVar env = new V1EnvVar();
        env.setName("appName");
        env.value(appName);
        envList.add(env);
        return envList;
    }


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
        podspec.setRestartPolicy("OnFailure");
        return podspec;
    }

    public V1PodTemplateSpec getPodTemplateSpec(V1ObjectMeta objectMetadata, V1PodSpec podSpec, HashMap<String, String> labels) {
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
        objectMetadata.setLabels(labels);
        podTemplateSpec.setMetadata(objectMetadata);
        podTemplateSpec.setSpec(podSpec);
        return podTemplateSpec;
    }

    public V1JobSpec getJobSpec(Integer replicas, V1PodTemplateSpec podTemplateSpec, HashMap<String, String> labels) {
        V1LabelSelector lSpec = new V1LabelSelector();
        V1JobSpec spec = new V1JobSpec();
        lSpec.setMatchLabels(labels);
        //set time in seconds after which pod will go once it reaches  completion state
        spec.setTtlSecondsAfterFinished(30);
        spec.setParallelism(replicas);
        spec.setTemplate(podTemplateSpec);
        return spec;
    }

    public V1ObjectMeta getDeploymentPodMetadata(String appName, HashMap<String, String> labels) {
        V1ObjectMeta podObjectMetaData = new V1ObjectMeta();
        podObjectMetaData.setName(appName);
        podObjectMetaData.setLabels(labels);
        return podObjectMetaData;
    }


    public V1CronJob getCronJobBody(V1ObjectMeta jobObjectMetaData, V1CronJobSpec jobSpec) {
        V1CronJob body = new V1CronJob();
        body.setKind("CronJob");
        body.setApiVersion("batch/v1");
        body.setMetadata(jobObjectMetaData);
        body.setSpec(jobSpec);
        return body;
    }

    public V1JobTemplateSpec getJobTemplateSpec(V1ObjectMeta podMetaData, V1JobSpec jobSpec) {
        V1JobTemplateSpec spec = new V1JobTemplateSpec();
        spec.setMetadata(podMetaData);
        spec.setSpec(jobSpec);
        return spec;
    }

    public V1CronJobSpec getCronJobSpec(String cronExpression, V1JobTemplateSpec podTemplateSpec) {
        V1CronJobSpec spec = new V1CronJobSpec();
        spec.setSchedule(cronExpression);
        spec.setConcurrencyPolicy("Allow");
        spec.setSuspend(false);
        spec.setJobTemplate(podTemplateSpec);
        return spec;
    }

}
