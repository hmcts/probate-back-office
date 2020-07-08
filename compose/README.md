# To use ccd-docker elements

## Purpose
Guidance on how to set up probate locally using the updated docker images.

##### 1) Install https://stedolan.github.io/jq/ 

```bash
  sudo apt-get install jq
```

For mac 
```bash
  brew install jq
```

NB. If you download the binary version it is called 'jq-osx-amd64' and the scripts later will fail because they are looking for 'jq'. 

##### 2) Login to azure

```bash
  az login
  az acr login --name hmctspublic --subscription DCD-CNP-Prod
  az acr login --name hmctsprivate --subscription DCD-CNP-Prod
```

##### 3) Reset your docker images, containers etc. 

```bash
   docker container rm $(docker container ls -a -q)
   docker image rm $(docker image ls -a -q)
   docker volume rm $(docker volume ls -q)
```

NB. Docker for desktop on a mac only allocates 2GB of memory by default, this is not enough I increased mine 16GB.

##### 4) Run environments script
```bash
   ./ccd login
```

For mac: 
```bash
    source ./bin/set-environment-variables.sh
```
For linux
```bash
   source ./bin/linux-set-environment-variables.sh
```
##### 4.1) setup logstash
 
In order to work locally on probate-frontend you will need to clone project ccd-logstash from github.
Checkout the probate-conf branch and build the docker image

```
   git checkout probate-conf
   docker build . -t ccd-logstash:probate
```   
 In probate-back-office/compose/elasticsearch.yml replace
   image: hmcts/ccd-logstash:latest with image: "ccd-logstash:probate"
   
##### 5) Start up docker 
```bash
   docker network create compose_default
   ./ccd compose pull
   ./ccd compose build
   ./ccd compose up
```

##### 6) Create blob store container
Once docker has started run
```bash
   ./bin/document-management-store-create-blob-store-container.sh
```

##### 7) Restart dm-store container
Find id of dm-store container
```bash
   docker ps | grep dm-store_1
```
Use id to stop container
```bash
   docker stop compose_dm-store_1_id
```

Start the dm-store container
```bash
   ./ccd compose up -d dm-store
```

#### 7.1) Restart other containers
On linux I had to restart 
* dm-store
* fees-api
* payments-api
* sidam-api

In that order

##### 8) Setup IDAM data.
```bash
   ./bin/idam-client-setup.sh
```

To check the IDAM data, you can log into IDAM-web `http://localhost:8082/login` with `idamOwner@hmcts.net/Ref0rmIsFun`.

##### 9) Generate roles, json->xls and import

###### Create roles and users
```bash
   ./bin/ccd-add-all-roles.sh
```
You can check the user and roles on the IDAM-web by searching for `ProbateSolCW1@gmail.com` on Manager Users page.

###### Generate xls 
For mac
```bash
   ./ccdImports/conversionScripts/createAllXLS.sh docker.for.mac.localhost:4104
```

For linux (replace ip with your own ip)
```bash
   ./ccdImports/conversionScripts/createAllXLS.sh $MY_IP:4104 
```

###### Import xls
```bash
   ./ccdImports/conversionScripts/importAllXLS.sh
```
##### 10) Start your local service 
###### Pull probate specific ccd-logstash
pull ccd-logstash branch probate-conf locally then

docker build . -t ccd-logstash:probate

###### Probate-back-office
Login to ccd on `http://localhost:3451`. Caseworker: `ProbateSolCW1@gmail.com / Pa55word11`. Solicitor  `ProbateSolicitor1@gmail.com / Pa55word11`.

Start logstash-probateman (for legacy cases)
```bash
   sudo /usr/share/logstash/bin/logstash -f logstash/legacy-case-data-local.conf
```

Run probate-back-office app. You can a doc in dm by going to `localhost:3453/documents/[**ID**]/binary `. 

###### Probate-frontend
Add keyword to fees database
```bash
    ./bin/fees-add-keyword.sh
```

Go to `probate-frontend/app/config.js` and update to `useIDAM: process.env.USE_IDAM || 'true'`. 

When `USE_IDAM` is true to regester you will need to change the call back port from 9002 to 3501

Start probate-frontend app. Follow `http:localhost:3000`. Login using `testusername@test.com/Pa55word11`.


## Linking to a Probate-frontend PR
You must link a probate-frontend pr to a probate-orchestrator pr and that to your probate-backoffice pr
* Create a PR off master for probate-orchestrator-service
* Use the PR number of the BO build in values.yml. Replace:
```
BACK_OFFICE_API_URL: "http://probate-back-office-pr-1101.service.core-compute-preview.internal"
```
* upgrade the Chart.yaml version in probate-orchestrator-service
```
version: 1.0.1
```
* Create a PR off master for probate-frontend
* Use the PR number of the Orchestrator build in values.yml. Replace:
```
ORCHESTRATOR_SERVICE_URL : http://probate-orchestrator-service-pr-334.service.core-compute-preview.internal
```
* upgrade the Chart.yaml version in probate-frontend
```
version: 2.0.14
```
* Build the 2 PRs above
* For probate-frontend access, go to (use the pr number you just created for fe):
```
https://probate-frontend-pr-1218.service.core-compute-preview.internal/start-eligibility
```
#####REMEMBER
######Remove your unwanted FE/ORCH PRs when you have finished QA

## Linking to a Caveats Frontend PR
Exactly the same as above, except you need to link on the probate-caveats-frontend PR
* Create a PR off master for probate-orchestrator-service
* Use the PR number of the BO build in values.yml. Replace:
```
BACK_OFFICE_API_URL: "http://probate-back-office-pr-1101.service.core-compute-preview.internal"
```
* upgrade the Chart.yaml version in probate-orchestrator-service
```
version: 1.0.1
```
* Create a PR off master for probate-caveats-frontend
* Use the PR number of the Orchestrator build in values.yml. Replace:
```
ORCHESTRATOR_SERVICE_URL : http://probate-orchestrator-service-pr-334.service.core-compute-preview.internal
```
* upgrade the Chart.yaml version in probate-caveats-frontend
```
version: 2.0.14
```
* Build the 2 PRs above
* For probate-caveats-frontend access, go to (use the pr number you just created for fe):
```
https://probate-caveats-fe-pr-276.service.core-compute-preview.internal/caveats/start-apply```
```

