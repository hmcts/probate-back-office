#!/bin/bash

PGPASSWORD=fees_register psql fees_register  -h localhost -d fees_register -p 5182 -c "UPDATE public.fee SET keyword = 'NewFee' WHERE code = 'FEE0003'"
