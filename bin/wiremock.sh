#!/usr/bin/env bash

# Setup Wiremock responses for Professional Reference Data based on existing Idam users

# pba account successful
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "url": "/credit-account-payments",
            "bodyPatterns": [ {
              "contains": "PBA0082126"
              }
            ],
            "headers": {
              "Content-Type": {
                "equalTo": "application/json"
              }
            }
          },
          "response": {
            "status": 201,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
                "reference": "RC-1590-6786-1063-9996",
                "date_created": "2020-05-28T15:10:10.694+0000",
                "status": "Success",
                "payment_group_reference": "2020-1590678609071",
                "status_histories": [
                  {
                    "status": "success",
                    "date_created": "2020-05-28T15:10:10.700+0000",
                    "date_updated": "2020-05-28T15:10:10.700+0000"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# pba account payment failure
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "url": "/credit-account-payments",
            "bodyPatterns": [ {
              "contains": "PBA0083372"
              }
            ],
            "headers": {
              "Content-Type": {
                "equalTo": "application/json"
              }
            }
          },
          "response": {
            "status": 403,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "reference": "RC-1599-4778-4711-5958",
              "date_created": "2020-09-07T11:24:07.160+0000",
              "status": "failed",
              "payment_group_reference": "2020-1599477846961",
              "status_histories": [
                {
                  "status": "failed",
                  "error_code": "CA-E0004",
                  "error_message": "Your account is deleted",
                  "date_created": "2020-09-07T11:24:07.169+0000",
                  "date_updated": "2020-09-07T11:24:07.169+0000"
                }
              ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# pba account on hold
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "url": "/credit-account-payments",
            "bodyPatterns": [ {
              "contains": "PBA0083374"
              }
            ],
            "headers": {
              "Content-Type": {
                "equalTo": "application/json"
              }
            }
          },
          "response": {
            "status": 403,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
                "reference": "RC-1590-6786-1063-9996",
                "date_created": "2020-05-28T15:10:10.694+0000",
                "status": "Failed",
                "payment_group_reference": "2020-1590678609071",
                "status_histories": [
                  {
                    "status": "failed",
                    "error_code": "CA-E0004",
                    "error_message": "Your account is on hold",
                    "date_created": "2020-05-28T15:10:10.700+0000",
                    "date_updated": "2020-05-28T15:10:10.700+0000"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# pba account on hold
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "url": "/credit-account-payments",
            "bodyPatterns": [ {
              "contains": "1000000"
              }
            ],
            "headers": {
              "Content-Type": {
                "equalTo": "application/json"
              }
            }
          },
          "response": {
            "status": 403,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
                "reference": "RC-1590-6786-1063-9996",
                "date_created": "2020-05-28T15:10:10.694+0000",
                "status": "Failed",
                "payment_group_reference": "2020-1590678609071",
                "status_histories": [
                  {
                    "status": "failed",
                    "error_code": "CA-E0004",
                    "error_message": "PBA account THE J M PRACTICE LTD have insufficient funds available",
                    "date_created": "2020-05-28T15:10:10.700+0000",
                    "date_updated": "2020-05-28T15:10:10.700+0000"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# duplicate payment
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "url": "/credit-account-payments",
            "bodyPatterns": [ {
              "contains": "999900"
              }
            ],
            "headers": {
              "Content-Type": {
                "equalTo": "application/json"
              }
            }
          },
          "response": {
            "status": 400,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
                "reference": "RC-1590-6786-1063-9996",
                "date_created": "2020-05-28T15:10:10.694+0000",
                "status": "Failed",
                "payment_group_reference": "2020-1590678609071",
                "status_histories": [
                  {
                    "status": "failed",
                    "error_code": "CA-E0004",
                    "error_message": "duplicate payment",
                    "date_created": "2020-05-28T15:10:10.700+0000",
                    "date_updated": "2020-05-28T15:10:10.700+0000"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

#PBA accounts
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "url": "/refdata/external/v1/organisations/pbas?email=probatesolicitor1@gmail.com"
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationEntityResponse" : {
                "organisationIdentifier": "0UFUG4Z",
                "name": "ia-legal-rep-org",
                "status": "ACTIVE",
                "sraId": null,
                "sraRegulated": false,
                "companyNumber": null,
                "companyUrl": null,
                "superUser": {
                  "firstName": "legalrep",
                  "lastName": "orgcreator",
                  "email": "superuser@probate-test.com"
                },
                "paymentAccount": [
                  "PBA0082126",
                  "PBA0083372",
                  "PBA0083374"
                ],
                "contactInformation": null
              }
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

#PBA NO accounts
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "url": "/refdata/external/v1/organisations/pbas?email=probatesolicitor2@gmail.com"
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationEntityResponse" : {
                "organisationIdentifier": "0UFUG4Z",
                "name": "ia-legal-rep-org",
                "status": "ACTIVE",
                "sraId": null,
                "sraRegulated": false,
                "companyNumber": null,
                "companyUrl": null,
                "superUser": {
                  "firstName": "legalrep",
                  "lastName": "orgcreator",
                  "email": "superuser@probate-test.com"
                },
                "paymentAccount": [
                ],
                "contactInformation": null
              }
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# make responses persistent in Docker volume
curl -X POST http://localhost:8991/__admin/mappings/save
