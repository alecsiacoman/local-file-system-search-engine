@echo off

echo Starting Worker 1 on port 3901...
start cmd /k mvn spring-boot:run -DskipTests=true -Dspring-boot.run.main-class=bruteforce.worker.WorkerApplication -Dspring-boot.run.arguments="--server.port=3901 --worker.directory=C:\Users\coman\Desktop\search-engine-test\dir1"

echo Starting Worker 2 on port 3902...
start cmd /k mvn spring-boot:run -DskipTests=true -Dspring-boot.run.main-class=bruteforce.worker.WorkerApplication -Dspring-boot.run.arguments="--server.port=3902 --worker.directory=C:\Users\coman\Desktop\search-engine-test\dir2"

echo Starting Master on port 3000...
start cmd /k mvn spring-boot:run -DskipTests=true -Dspring-boot.run.main-class=bruteforce.master.MasterApplication -Dspring-boot.run.arguments="--server.port=3000"