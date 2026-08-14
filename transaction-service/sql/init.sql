-- transactions init
CREATE TABLE IF NOT EXISTS transactions (
  id UUID PRIMARY KEY,
  user_id BIGINT NOT NULL,
  description VARCHAR(500) NOT NULL,
  amount NUMERIC(19,2) NOT NULL,
  original_amount NUMERIC(19,2),
  currency VARCHAR(10) NOT NULL,
  category VARCHAR(100) NOT NULL,
  type VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

