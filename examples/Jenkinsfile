/**
 * We don't set an agent, because we don't want to block an executor.
 */

pipeline {
    agent none
    stages {
        stage("Trigger Bamboo") {
            steps {
                // "bamboo-test-credentials" must be set at the folder or global levels.
                timeout(time: 600, unit: 'SECONDS') {   // Combine with a 10 minute timeout
                    withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                                      credentialsId   : "bamboo-test-credentials",
                                      usernameVariable: 'BAMBOO_USER',
                                      passwordVariable: 'BAMBOO_PASS']]) {
                        buildBamboo(projectKey: "projectKey",
                                planKey: "planKey",
                                serverAddress: 'http://bamboo-server',
                                'username': env.BAMBOO_USER,
                                'password': env.BAMBOO_PASS,
                                propagate: False,
                                checkInterval: 120,
                                params: ["appVersion": "1.0.0", "buildNumber": env.BUILD_NUMBER])
                    }
                }
            }
        }
    }
}


