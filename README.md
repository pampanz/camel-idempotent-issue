# Apache Camel Test of idempotent file store

The release 2.20.1 seems to have changed the behaviour or have a bug with idempotent file stores. This is a simple project to verify that behaviour in isolation.

This behaviour has been verified on Linux and OSX (not tried on Windows but doesn't appear to be dependent of the OS) 

## Execution and assembly with at the root of the project
./gradlew clean run

## Verifying behaviour can be achieved
1. Before starting the server, delete any pre-existing details from previous run

          rm -rf fromSource* toTarget idempotent.file.store dedup.idempotent.file.store testIdempotent.log; mkdir fromSource fromSource2 

2. Choose which version of camel to test, by changing the version of the dependency on build.gradle

       // Use 2.20.0 to reproduce fail          
       dependencies {
        compile "org.slf4j:slf4j-api:1.7.25"
        compile "org.slf4j:jcl-over-slf4j:1.7.25"
        compile "ch.qos.logback:logback-core:1.2.2"
        compile "ch.qos.logback:logback-classic:1.2.2"
        compile "org.apache.camel:camel-core:2.20.0"
        compile "org.codehaus.groovy:groovy:2.4.12"
      
        compile "commons-io:commons-io:2.5"
      
        testCompile("org.spockframework:spock-core:1.0-groovy-2.4") {
          exclude group: "org.codehaus.groovy"
        }
       }

       // Use 2.19.0/4 to where it works          
       dependencies {
        compile "org.slf4j:slf4j-api:1.7.25"
        compile "org.slf4j:jcl-over-slf4j:1.7.25"
        compile "ch.qos.logback:logback-core:1.2.2"
        compile "ch.qos.logback:logback-classic:1.2.2"
        compile "org.apache.camel:camel-core:2.19.0"
        compile "org.codehaus.groovy:groovy:2.4.12"
      
        compile "commons-io:commons-io:2.5"
      
        testCompile("org.spockframework:spock-core:1.0-groovy-2.4") {
          exclude group: "org.codehaus.groovy"
        }
       }


3. Run script to create files to be read on the source directories, the number choosen triggers the issue

          for each in {1..1002}; do                                                                                                                                                                                                    <<<
          echo "c1$each" >> fromSource/test${each};
          echo "c2$each" >> fromSource2/test${each};
          done

4. Run the server 
         ./gradlew clean run

  Log will appear in console and on the log file testIdempotent.log on the root of the project

4. Wait till first batch of messages is processed by monitoring the logs and the folders
check with:
4.1 

         ls toTarget |wc -l shold return 1002
         
4.2 

         wc -l idempotent.file.store dedup.idempotent.file.store 

should show 1002 on each store

5. Run the script above again to generate the same files over in the input directory

        for each in {1..1002}; do                                                                                                                                                                                                    <<<
        echo "c1$each" >> fromSource/test${each};
        echo "c2$each" >> fromSource2/test${each};
        done

6. Finally, check again the toTarget and idempotent stores. They should not have changed in size as all the files are the same, and have already been processed
Note on 2.20.* the stores and target starts to grow in size at this point the server can be stopped.
