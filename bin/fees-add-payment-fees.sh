#!/bin/bash

PGPASSWORD=fees_register psql fees_register  -h localhost -d fees_register -p 5182 -c "INSERT INTO fee (application_type,channel_type,code,creation_time,event_type,fee_number,fee_type,jurisdiction1,jurisdiction2,keyword,last_updated,service,unspecified_claim_amount) values ('all','default','FEE0288',now(),'miscellaneous',288,'FixedFee','family','probate registry','MNO',now(),'probate',false)"

PGPASSWORD=fees_register psql fees_register  -h localhost -d fees_register -p 5182 -c "INSERT INTO amount (amount_type,creation_time,last_updated) VALUES ('FlatAmount',now(),now())"

PGPASSWORD=fees_register psql fees_register  -h localhost -d fees_register -p 5182 -c "INSERT INTO flat_amount(id, amount) VALUES ((SELECT MAX( id ) FROM amount a ), 3)"

PGPASSWORD=fees_register psql fees_register  -h localhost -d fees_register -p 5182 -c "INSERT INTO fee_version (description,status,valid_from,valid_to,"version",amount_id,fee_id,direction_type,fee_order_name,memo_line,natural_account_code,si_ref_id,statutory_instrument,approved_by,author) VALUES ('Application for the entry or extension of a caveat',1,'2011-04-03 00:00:00.000',NULL,1,(SELECT MAX( id ) FROM amount a ),(SELECT MAX(id) from fee),'cost recovery','Non-Contentious Probate Fees','RECEIPT OF FEES - Family misc probate','4481102173','4','2011 No 588 ','126175','126172')"
