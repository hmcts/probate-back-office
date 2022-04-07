#!/bin/bash
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
        "organisationIdentifier": "XXXXX"
    }
  }
}
' \
http://localhost:8991/__admin/mappings/new
