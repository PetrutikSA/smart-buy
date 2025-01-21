CREATE TABLE IF NOT EXISTS conversation (
  chat_id BIGINT NOT NULL,
  status VARCHAR(16),
  request_added SMALLINT,
  client_input VARCHAR,
  CONSTRAINT pk_users PRIMARY KEY (chat_id)
);