#!/bin/bash

#Jenkins
# http://3.37.2.69:8080
#Dev
# http://3.39.89.212:8080
#Staging
# http://43.200.13.189:8080
#Prod
# http://43.203.82.227:8080
scp -i joblog-key.pem .env.dev ubuntu@3.37.2.69:/home/ubuntu/jenkins/jenkins_home/workspace/joblog/.env.dev