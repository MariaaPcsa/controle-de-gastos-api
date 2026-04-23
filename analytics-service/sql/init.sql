-- analytics init
CREATE TABLE IF NOT EXISTS expenses (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  category VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  amount NUMERIC(19,2) NOT NULL,
  date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_aggregates (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  date DATE NOT NULL,
  total NUMERIC(19,2) NOT NULL
);

