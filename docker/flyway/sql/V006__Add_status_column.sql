ALTER TABLE letters
ADD COLUMN status VARCHAR(256);

UPDATE
  letters
SET
  status = (
    CASE
      WHEN printed_at IS NULL THEN
        'Uploaded'
      ELSE
        'Posted'
    END
  );
