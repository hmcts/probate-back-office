#!/bin/bash
# Setup Wiremock responses for Professional Reference Data based on existing Idam users
probatesolicitortestorgtest1=$1
probatesolicitortestorg2test1=$2
probatesolicitortestorgtestman=$3

# clear all existing
curl -X 'DELETE' 'http://localhost:8991/__admin/mappings' -H 'accept: */*'

# pba account successful
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "urlPath": "/credit-account-payments",
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
                "reference": "RC-1590-6786-1063-9991",
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
            "urlPath": "/credit-account-payments",
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
              "reference": "RC-1590-6786-1063-9992",
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
            "urlPath": "/credit-account-payments",
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
                "reference": "RC-1590-6786-1063-9993",
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

# pba insufficient payments
curl -X POST \
--data '{
          "request": {
            "method": "POST",
            "urlPath": "/credit-account-payments",
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
                "reference": "RC-1590-6786-1063-9994",
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
            "urlPath": "/credit-account-payments",
            "bodyPatterns": [ {
              "contains": "999999"
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
                "reference": "RC-1590-6786-1063-9995",
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

#PBA accounts Success + deleted
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "urlPath": "/refdata/external/v1/organisations/pbas",
            "headers": {
              "UserEmail": {
                "equalTo": "probatesolicitortestorgtest1@gmail.com"
              }
            }
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationEntityResponse" : {
                "organisationIdentifier": "XXXXX",
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
                  "PBA0083372"
                ],
                "contactInformation": null
              }
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new
#PBA accounts ON HOLD
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "urlPath": "/refdata/external/v1/organisations/pbas",
            "headers": {
              "UserEmail": {
                "equalTo": "probatesolicitortestorg2test1@gmail.com"
              }
            }
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationEntityResponse" : {
                "organisationIdentifier": "XXXXX",
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
            "urlPath": "/refdata/external/v1/organisations/pbas",
            "headers": {
              "UserEmail": {
                "equalTo": "probatesolicitor2@gmail.com"
              }
            }
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

#Organisations
curl -X POST \
--data '{
  "request": {
    "method": "GET",
    "urlPath": "/refdata/external/v1/organisations"
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "jsonBody": 
      {
      "organisationIdentifier": "XXXXX",
      "name": "Probate Test Org",
      "contactInformation": [
          {
            "addressLine1": "string",
            "addressLine2": "string",
            "addressLine3": "string",
            "country": "string",
            "county": "string",
            "postCode": "string",
            "townCity": "string"
          }
          ],
          "paymentAccount": [
           "PBA0082126",
            "PBA0083372"
        ],
    "organisations": [
      {
        "companyNumber": "string",
        "companyUrl": "string",
        "contactInformation": [
          {
            "addressLine1": "string",
            "addressLine2": "string",
            "addressLine3": "string",
            "country": "string",
            "county": "string",
            "postCode": "string",
            "townCity": "string"
          }
        ],
        "name": "Probate Test Org",
        "organisationIdentifier": "XXXXX",
        "paymentAccount": [
          "string"
        ],
        "sraId": "string",
        "sraRegulated": true,
        "status": "ACTIVE",
        "superUser": {
          "email": "probatesolicitortestorgman3@gmail.com",
          "firstName": "PBA",
          "lastName": "TestUser"
        }
      }
    ]
  }
  }
}
' \
http://localhost:8991/__admin/mappings/new

#OrganisationAddress
curl -X POST \
--data '{
  "request": {
    "method": "GET",
    "urlPath": "/refdata/external/v1/organisations",
    "queryParameters": {
      "id": {
        "equalTo": "XXXXX"
      }
    }
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "jsonBody": {
      "contactInformation": [
        {
          "addressLine1": "Line 1",
          "addressLine2": "Line 2",
          "addressLine3": "Line 3",
          "county": "Kent",
          "townCity": "London",
          "country": "UK",
          "postCode": "DA15 7LN"
        }
      ],
      "organisationIdentifier": "XXXXX",
      "name": "Probate Test Org"
    }
  }
}
' \
http://localhost:8991/__admin/mappings/new

curl -X POST \
--data '{
  "request": {
    "method": "GET",
    "urlPath": "/health"
  },
  "response": {
    "status": 200
  }
}
' \
http://localhost:8991/__admin/mappings/new

#OrganisationAddress2
curl -X POST \
--data '{
  "request": {
    "method": "GET",
    "urlPath": "/refdata/external/v1/organisations/status/ACTIVE",
    "queryParameters": {
      "address": {
        "equalTo": "true"
      }
    }
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "jsonBody": {
      "contactInformation": [
        {
          "addressLine1": "Line 1",
          "addressLine2": "Line 2",
          "addressLine3": "Line 3",
          "county": "Kent",
          "townCity": "London",
          "country": "UK",
          "postCode": "DA15 7LN"
        }
      ],
      "organisationIdentifier": "XXXXX",
      "name": "Probate Test Org"
    }
  }
}
' \
http://localhost:8991/__admin/mappings/new

#OrganisationUsers
curl -X POST \
--data '{
  "request": {
    "method": "GET",
    "urlPath": "/refdata/internal/v1/organisations/XXXXX/users",
    "queryParameters": {
      "returnRoles": {
        "equalTo": "false"
      }
    }
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "jsonBody": {
      "users": [
        {
          "userIdentifier": "'${probatesolicitortestorgtest1}'",
          "firstName": "PBA",
          "lastName": "TestUser",
          "email": "probatesolicitortestorgtest1@gmail.com",
          "idamStatus": "ACTIVE"
        },
        {
          "userIdentifier": "'${probatesolicitortestorg2test1}'",
          "firstName": "PBA",
          "lastName": "TestUser2",
          "email": "probatesolicitortestorg2test1@gmail.com",
          "idamStatus": "ACTIVE"
        },
        {
          "userIdentifier": "'${probatesolicitortestorgtestman}'",
          "firstName": "PBA",
          "lastName": "TestOrg3",
          "email": "probatesolicitortestorgman3@gmail.com",
          "idamStatus": "ACTIVE"
        }
      ],
      "organisationIdentifier": "XXXXX"
    }
  }
}' \
http://localhost:8991/__admin/mappings/new


#Users no roles
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "urlPath": "/refdata/external/v1/organisations/users",
            "queryParameters": {
              "status": {
                "equalTo": "Active"
              },
              "returnRoles": {
                  "equalTo": "false"
                }
            }
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationIdentifier": "XXXXX",
              "name": "Probate Test Org",
              "users": [
                  {
                    "userIdentifier": "'${probatesolicitortestorgtestman}'",
                    "firstName": "PBA",
                    "lastName": "TestOrg3",
                    "email": "probatesolicitortestorgman3@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager",
                      "pui-caa",
                      "pui-organisation-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  },
                  {
                    "userIdentifier": "'${probatesolicitortestorgtest1}'",
                    "firstName": "PBA",
                    "lastName": "TestUser",
                    "email": "probatesolicitortestorgtest1@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  },
                  {
                    "userIdentifier": "'${probatesolicitortestorg2test1}'",
                    "firstName": "PBA",
                    "lastName": "TestUser2",
                    "email": "probatesolicitortestorg2test1@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

#Users with roles
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "urlPath": "/refdata/external/v1/organisations/users",
            "queryParameters": {
              "status": {
                "equalTo": "active"
              },
              "returnRoles": {
                  "equalTo": "true"
                }
            }
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationIdentifier": "XXXXX",
              "name": "Probate Test Org",
              "users": [
                  {
                    "userIdentifier": "'${probatesolicitortestorgtestman}'",
                    "firstName": "PBA",
                    "lastName": "TestOrg3",
                    "email": "probatesolicitortestorgman3@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager",
                      "pui-caa",
                      "pui-organisation-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  },
                  {
                    "userIdentifier": "'${probatesolicitortestorgtest1}'",
                    "firstName": "PBA",
                    "lastName": "TestUser",
                    "email": "probatesolicitortestorgtest1@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  },
                  {
                    "userIdentifier": "'${probatesolicitortestorg2test1}'",
                    "firstName": "PBA",
                    "lastName": "TestUser2",
                    "email": "probatesolicitortestorg2test1@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

#Users no roles
curl -X POST \
--data '{
          "request": {
            "method": "GET",
            "urlPath": "/refdata/external/v1/organisations/users/"
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
              "organisationIdentifier": "XXXXX",
              "name": "Probate Test Org",
              "users": [
                  {
                    "userIdentifier": "'${probatesolicitortestorgtestman}'",
                    "firstName": "PBA",
                    "lastName": "TestOrg3",
                    "email": "probatesolicitortestorgman3@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager",
                      "pui-caa",
                      "pui-organisation-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  },
                  {
                    "userIdentifier": "'${probatesolicitortestorgtest1}'",
                    "firstName": "PBA",
                    "lastName": "TestUser",
                    "email": "probatesolicitortestorgtest1@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  },
                  {
                    "userIdentifier": "'${probatesolicitortestorg2test1}'",
                    "firstName": "PBA",
                    "lastName": "TestUser2",
                    "email": "probatesolicitortestorg2test1@gmail.com",
                    "roles": [
                      "caseworker",
                      "caseworker-probate",
                      "caseworker-probate-solicitor",
                      "pui-user-manager",
                      "pui-case-manager"
                    ],
                    "idamStatus": "ACTIVE",
                    "idamStatusCode": "200",
                    "idamMessage": "11 OK"
                  }
                ]
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# make responses persistent in Docker volume
curl -X POST http://localhost:8991/__admin/mappings/save

curl -X 'GET' 'http://localhost:8991/__admin/mappings?limit=100&offset=0' -H 'accept: application/json'
