# ccd-definition-processor

## Purpose

The purpose of these scripts is to convert the existing CCD templates to json and vice versa

## Installation

install the docker image https://github.com/hmcts/ccd-definition-processor  

```docker pull hmcts/ccd-definition-processor:latest```

Set the IP address of the ccd service as an environment variable, if needed. This will default to the `docker.for.mac.localhost` and port for MacOS and the ip bound to the `tun0` interface for linux.

```export CCD_DEF_CASE_SERVICE_BASE_URL=docker.for.mac.localhost:4104```

## Usage

### Convert Excel files to json

run the `./convertXLSToJson.sh` script, passing the excel file to be converted as an argument to the script. This will output json definition files to the `xlsToJson` folder

### Convert json files to Excel

to convert the json files back to excel, run the `./convertJsonToXLS.sh` script passing the folder, from the above conversion, as an argument to the script. For Example

```./convertJsonToXLS.sh xlsToJson/back-office/CCD_Probate_V04.46-Dev/```

### Change hardcoded ip in excel files to variable substitution format

In some cases, the IP address and ports are hardcoded in an excel file to that of a particular developer machine. For variable substitution to work in the `./convertJsonToXLS.sh` script, run the `setupIP.sh` script.

For example, the following command

```./setupIP.sh ./xlsToJson/CCD_Probate_V05.03-Dev 10.99.2.255:4104```

will change all references to the hardcoded ip and port `(10.99.2.255:4104)` in the json output files, to that of the variable substitution format `${CCD_DEF_CASE_SERVICE_BASE_URL}`. These json files can then be fed to the `./convertJsonToXLS.sh` script, for conversion back to excel with the appropriate URL
