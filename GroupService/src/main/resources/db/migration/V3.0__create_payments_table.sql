CREATE TABLE payments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  payment_description VARCHAR(200) NOT NULL,
  price FLOAT NOT NULL,
  co_payers VARCHAR(255),
  creator_id BIGINT NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  group_id BIGINT,
  FOREIGN KEY (group_id) REFERENCES groups (id)
);
