CREATE TABLE letters (
  id UUID NOT NULL PRIMARY KEY,
  message_id VARCHAR(256) NOT NULL,
  service VARCHAR(256) NOT NULL,
  is_failed BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL,
  sent_to_print_at TIMESTAMP NULL,
  printed_at TIMESTAMP NULL,
  type VARCHAR(256) NOT NULL,
  additional_data JSON NULL,
  status VARCHAR(256),
  file_content BYTEA,
  is_encrypted BOOLEAN DEFAULT FALSE
);
