package com.salik.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class Configurations {

    public ApiClient getApiClientConf() throws IOException {
        String url = System.getenv("K8S_URL");
        String token = System.getenv("K8S_TOKEN");
        ApiClient defaultClient = null;
        String runEnv = System.getenv().getOrDefault("K8S_ENVIRONMENT", "local");

        switch (runEnv) {
            case "cluster":
                defaultClient = Config.fromCluster();
                break;
            case "routing":
                defaultClient = Config.fromToken(url, token, false);
                break;
            case "local":
                defaultClient = Config.defaultClient();
                break;
        }

        defaultClient.setConnectTimeout(600000);
        defaultClient.setReadTimeout(600000);
        defaultClient.setDebugging(true);
        return defaultClient;
    }
}
