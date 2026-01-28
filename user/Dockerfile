version: "3.8"

services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: usersdb
      POSTGRES_USER: root
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"

  user-service:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      - postgres
