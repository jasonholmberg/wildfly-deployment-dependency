# WildFly inter-war dependency problem #

This project is provide a simple illustration of how an inter-war can cause a war deployment to fail when the war it has declared an a dependency through jboss-deployment-structure.xml is redeployed.

This seem to only happen under the following conditions:

1. At least two WARs are deployed in the same container.
2. App 2 has at least one Persistence Unit
3. App 2 depends on the App 1 deployment (via jboss-deployment-structure.xml)
4. Deploy both applications.
5. Redeploy App 1.
6. See App 2 fail. It becomes unavailable.

## Building these sample applications.
The gradle build expects to find a property named `wildflyHome` used in the deploy task. This should point to the directory of your WildFly install. If you are familiar with Gradle, then setting up this property will be easy. 