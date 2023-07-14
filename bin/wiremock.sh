#!/bin/bash
binFolder=$(dirname "$0")
# Setup Wiremock responses for Professional Reference Data based on existing Idam users
user1Token=$(${binFolder}/idam-lease-user-token.sh probatesolicitortestorgtest1@gmail.com Probate123)
probatesolicitortestorgtest1=$(curl -X GET "http://localhost:5000/details" -H  "accept: application/json" -H  "authorization: ${user1Token}" | jq -r .id)
echo probatesolicitortestorgtest1=$probatesolicitortestorgtest1

user2Token=$(${binFolder}/idam-lease-user-token.sh probatesolicitortestorg2test1@gmail.com Probate123)
probatesolicitortestorg2test1=$(curl -X GET "http://localhost:5000/details" -H  "accept: application/json" -H  "authorization: ${user2Token}" | jq -r .id)
echo probatesolicitortestorg2test1=$probatesolicitortestorg2test1

orgUserToken=$(${binFolder}/idam-lease-user-token.sh probatesolicitortestorgman3@gmail.com Probate123)
probatesolicitortestorgtestman=$(curl -X GET "http://localhost:5000/details" -H  "accept: application/json" -H  "authorization: ${orgUserToken}" | jq -r .id)
echo probatesolicitortestorgtestman=$probatesolicitortestorgtestman


# clear all existing
echo clearing all wiremock setup
curl -X 'DELETE' 'http://localhost:8991/__admin/mappings' -H 'accept: */*'

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
    "jsonBody": {
      "companyNumber": "string",
      "companyUrl": "string",
      "contactInformation": [
        {
          "addressLine1": "Line 1A",
          "addressLine2": "Line 2A",
          "addressLine3": "Line 3A",
          "county": "Kent",
          "townCity": "London",
          "country": "UK",
          "postCode": "DA15 7LN"
        }
      ],
      "name": "Probate Test Org",
      "organisationIdentifier": "XXXXX",
      "paymentAccount": [
        "PBA0082126",
        "PBA0083372"
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
            "method": "POST",
            "urlPath": "/service-request"
          },
          "response": {
            "status": 200,
            "headers": {
              "Content-Type": "application/json"
            },
            "jsonBody": {
               "service_request_reference": "abcdef123456"
            }
          }
        }' \
http://localhost:8991/__admin/mappings/new

# make responses persistent in Docker volume
curl -X POST http://localhost:8991/__admin/mappings/save

echo listing all wiremock setup
curl -X 'GET' 'http://localhost:8991/__admin/mappings?limit=100&offset=0' -H 'accept: application/json'
