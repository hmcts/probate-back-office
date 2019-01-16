CREATE TABLE message (
  id UUID NOT NULL PRIMARY KEY,
  message_id VARCHAR(256) NOT NULL,
  service VARCHAR(256) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  sent_to_print_at TIMESTAMP NULL,
  printed_at TIMESTAMP NULL,
  additional_data JSON NULL
);
