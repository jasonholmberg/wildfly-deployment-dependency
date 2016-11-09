# WildFly inter-war dependency problem when module is optional but present

This project is provide a simple illustration of how an inter-war dependency can cause a war deployment to fail when the war it has 
declared an a dependency through jboss-deployment-structure.xml is redeployed and all the dependent modules are `optional="true"` but present

If you set optional to false or leave it out completely, then deployment happen correctly no matter the order.

This seem to only happen under the following conditions:

 1. At least three WARs are deployed in the same container.
 1. App 2 has at least one Persistence Unit
 1. App 2 depends on the App 1 and App 3 deployments (via jboss-deployment-structure.xml)
 1. Both dependencies are marked `optional`
 1. With server down, deploy all applications
 1. Start server
 1. Redeploy all applications with server running
 1. See App 2 fails. It becomes unavailable.

This can also be triggers using the deployment triggers:

This seems to work fine:
```
$ touch app1.war.dodeploy app2.war.dodeploy app3.war.dodeploy
```

This seems to result in App 2 failing to deploy.
```
$ touch app2.war.dodeploy app3.war.dodeploy app1.war.dodeploy
```

## Building these sample applications.
The gradle build expects to find a property named `serverHome` used in the deploy task. This should point to the directory 
of your WildFly install. If you are familiar with Gradle, then setting up this property will be easy. 

Executing: `gradle clean deploy` from the root project will deploy all wars

## Environment
- **OS**: Windows or MacOSX
- **Java**: 1.8+
- **Server**: WildFly 10.1

## Observed Exceptions

```
12:29:06,030 INFO  [org.jboss.as.connector.subsystems.datasources] (MSC service thread 1-1) - WFLYJCA0001: Bound data source [java:jboss/datasources/App2XADS]
12:29:06,030 ERROR [org.jboss.msc.service.fail] (MSC service thread 1-4) - MSC000001: Failed to start service jboss.deployment.unit."app2.war".POST_MODULE: org.jboss.msc.service.StartException in service jboss.deployment.unit."app2.war".POST_MODULE: WFLYSRV0153: Failed to process phase POST_MODULE of deployment "app2.war"
    at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:154)
    at org.jboss.msc.service.ServiceControllerImpl$StartTask.startService(ServiceControllerImpl.java:1948)
    at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1881)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.RuntimeException: WFLYSRV0177: Error getting reflective information for class com.app2.servlet.App2Servlet with ClassLoader ModuleClassLoader for Module "deployment.app2.war:main" from Service Module Loader
    at org.jboss.as.server.deployment.reflect.DeploymentReflectionIndex.getClassIndex(DeploymentReflectionIndex.java:70)
    at org.jboss.as.ee.metadata.MethodAnnotationAggregator.runtimeAnnotationInformation(MethodAnnotationAggregator.java:57)
    at org.jboss.as.ee.component.deployers.InterceptorAnnotationProcessor.handleAnnotations(InterceptorAnnotationProcessor.java:106)
    at org.jboss.as.ee.component.deployers.InterceptorAnnotationProcessor.processComponentConfig(InterceptorAnnotationProcessor.java:91)
    at org.jboss.as.ee.component.deployers.InterceptorAnnotationProcessor.deploy(InterceptorAnnotationProcessor.java:76)
    at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:147)
    ... 5 more
Caused by: java.lang.NoClassDefFoundError: Lcom/app3/service/App3SomeService;
    at java.lang.Class.getDeclaredFields0(Native Method)
    at java.lang.Class.privateGetDeclaredFields(Class.java:2583)
    at java.lang.Class.getDeclaredFields(Class.java:1916)
    at org.jboss.as.server.deployment.reflect.ClassReflectionIndex.<init>(ClassReflectionIndex.java:72)
    at org.jboss.as.server.deployment.reflect.DeploymentReflectionIndex.getClassIndex(DeploymentReflectionIndex.java:66)
    ... 10 more
Caused by: java.lang.ClassNotFoundException: com.app3.service.App3SomeService from [Module "deployment.app2.war:main" from Service Module Loader]
    at org.jboss.modules.ModuleClassLoader.findClass(ModuleClassLoader.java:198)
    at org.jboss.modules.ConcurrentClassLoader.performLoadClassUnchecked(ConcurrentClassLoader.java:363)
    at org.jboss.modules.ConcurrentClassLoader.performLoadClass(ConcurrentClassLoader.java:351)
    at org.jboss.modules.ConcurrentClassLoader.loadClass(ConcurrentClassLoader.java:93)
    ... 15 more

12:29:06,030 INFO  [org.jboss.as.jpa] (ServerService Thread Pool -- 85) - WFLYJPA0010: Starting Persistence Unit (phase 1 of 2) Service 'app2.war#MyApp2PU'
12:29:06,032 INFO  [org.hibernate.jpa.internal.util.LogHelper] (ServerService Thread Pool -- 85) - HHH000204: Processing PersistenceUnitInfo [
    name: MyApp2PU
    ...]
12:29:06,062 INFO  [org.jboss.weld.deployer] (MSC service thread 1-8) - WFLYWELD0003: Processing weld deployment app3.war
12:29:06,136 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 85) - WFLYUT0021: Registered web context: /app1
12:29:06,209 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 85) - WFLYUT0021: Registered web context: /app3
12:29:06,209 ERROR [org.jboss.as.controller.management-operation] (DeploymentScanner-threads - 2) - WFLYCTL0013: Operation ("full-replace-deployment") failed - address: ([]) - failure description: {
    "WFLYCTL0080: Failed services" => {"jboss.deployment.unit.\"app2.war\".POST_MODULE" => "org.jboss.msc.service.StartException in service jboss.deployment.unit.\"app2.war\".POST_MODULE: WFLYSRV0153: Failed to process phase POST_MODULE of deployment \"app2.war\"
    Caused by: java.lang.RuntimeException: WFLYSRV0177: Error getting reflective information for class com.app2.servlet.App2Servlet with ClassLoader ModuleClassLoader for Module \"deployment.app2.war:main\" from Service Module Loader
    Caused by: java.lang.NoClassDefFoundError: Lcom/app3/service/App3SomeService;
    Caused by: java.lang.ClassNotFoundException: com.app3.service.App3SomeService from [Module \"deployment.app2.war:main\" from Service Module Loader]"},
    "WFLYCTL0412: Required services that are not installed:" => ["jboss.deployment.unit.\"app2.war\".POST_MODULE"],
    "WFLYCTL0180: Services with missing/unavailable dependencies" => undefined
```