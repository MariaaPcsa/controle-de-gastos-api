-- users init
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(200) UNIQUE NOT NULL,
  password VARCHAR(200) NOT NULL,
  type VARCHAR(50) NOT NULL,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now()
);

-- seed admin
INSERT INTO users (name, email, password, type, active) VALUES ('Admin','admin@example.com','$2a$10$XQe...', 'ADMIN', true) ON CONFLICT DO NOTHING;

