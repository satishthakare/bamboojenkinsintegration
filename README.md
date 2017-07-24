# Bamboo Pipeline Step Plugin

## Features

This plugin provides a step to call out to Bamboo plans.

## Usage

The plugin provides the **buildBamboo** step.  Wrap it within a **withCredentials** to supply authentication
information.  You can also wrap it within a **timeout** block.

```
timeout(time: 600, unit: 'SECONDS') { // change to a convenient timeout for you
    withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                      credentialsId   : "bamboo-test-credentials",
                      usernameVariable: 'BAMBOO_USER',
                      passwordVariable: 'BAMBOO_PASS']]) {
        buildBamboo(projectKey: "projectKey", planKey: "planKey", serverAddress: 'http://bamboo-server', 'username': env.BAMBOO_USER, 'password': env.BAMBOO_PASS)
    }
}
```

## Notes

The step uses a SynchronousNonBlockingStepExecution so it can run in the main thread without blocking execution.  This
is to prevent potentially long running jobs, which could take hours, from blocking an executor.  The main work done by
the plugin is to start the Bamboo plan, and then poll the target server every 30 seconds to monitor completion via the
REST API.