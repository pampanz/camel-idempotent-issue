# Apache Camel Test of idempotent file store

The release 2.20.1 seems to have changed the behaviour or have a bug with idempotent file stores. This is a simple project to verify that behaviour in isolation.

## Execution and assembly with
./gradlew clean run

## Using
echo "content" > fromSource/test

## check 
The file should go to fileTarget
and also to file store

cat idempotent.file.store


## check 3

1. Delete any pre-existing details from previous run
rm -rf fromSource* toTarget idempotent.file.store dedup.idempotent.file.store testIdempotent.log; mkdir fromSource fromSource2 
2. Choose which version of camel to test, by changing the dependency on build.gradle

3. Run script to create files to be read on the source directories
for each in {1..1002}; do                                                                                                                                                                                                    <<<
echo "c1$each" >> fromSource/test${each};
echo "c2$each" >> fromSource2/test${each};
done

4. Wait till first batch of messages is processed
check with:
4.1 ls toTarget |wc -l shold return 1002
4.2 wc -l idempotent.file.store dedup.idempotent.file.store should show 1002 on each store
5. Run the script above again to generate the same files over in the input directory
for each in {1..1002}; do                                                                                                                                                                                                    <<<
echo "c1$each" >> fromSource/test${each};
echo "c2$each" >> fromSource2/test${each};
done
6. Check again the toTarget and idempotent stores they shouldn't change in size as all the files are already in place
Note on 2.20.* the stores and target start to grow in size at this point the server can be stopped

Results
With 5000 files it fails on 2.20.0 camel and not in 2.19.4
With 100 files it doesn't fail on 2.20.0 camel nor in 2.19.4
With 1000 files it doesn't fail on 2.20.0 camel nor in 2.19.4
With 2000 files it fails on 2.20.0 camel and not in 2.19.4
