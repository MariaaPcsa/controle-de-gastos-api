# Sobre o Serviço;



                ┌──────────────────────┐
                │     API GATEWAY      │
                │ Spring Cloud Gateway │
                └──────────┬───────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ USER SERVICE │   │ TRANSACTION  │   │ ANALYTICS    │
│              │   │ SERVICE      │   │ SERVICE      │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
│                  │                  │
│                  │                  │
│        ┌─────────▼─────────┐        │
│        │      KAFKA        │◄───────┘
│        │  (EVENT BUS)      │
│        └─────────┬─────────┘
│                  │
│         ┌────────▼────────┐
│         │ ANALYTICS CONSUMER │
│         └──────────────────┘


# mvn clean package
mvn spring-boot:run

 # Cada serviço possui seu próprio banco PostgreSQL




🔥 POSSÍVEL OUTRO PROBLEMA (volume antigo)

Mesmo corrigindo isso, pode acontecer:

👉 banco não ser recriado por causa do volume

💣 Solução:
docker-compose down -v
docker-compose up -d --build