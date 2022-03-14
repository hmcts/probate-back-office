#!/bin/bash

curl -XPOST \
  http://localhost:5000/testing-support/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "email":"probatesolicitortestorg1test3@gmail.com",
    "forename":"ProbPrac",
    "surname":"User3",
    "password":"Probate123",
    "roles": [
      {"code": "caseworker"},
      {"code": "caseworker-probate"},
      {"code": "caseworker-probate-solicitor"},
      {"code": "caseworker-probate-superuser"},
      {"code": "pui-case-manager"},
      {"code": "pui-user-manager"},
      {"code": "caseworker-caa"}
      ]
    }'