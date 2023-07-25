package com.salik;

import com.salik.deployments.K8sDeploymentProcessor;

public class Starter {
    public static void main(String[] args) {


       K8sDeploymentProcessor deploymentManager = new K8sDeploymentProcessor();
       deploymentManager.createDeployment("deployment-demo-2");

       /* StatefulSetManager statefulSetManager = new StatefulSetManager();
        statefulSetManager.deleteStatefulSet();*/

       /* JobManager jobManager = new JobManager();
        jobManager.deleteJob();
*/
         //new CronJobManager().deleteCronJob();
    }

}
