CREATE TABLE users
(
   id SERIAL PRIMARY KEY,
   username TEXT NOT NULL,
   password TEXT NOT NULL,
   enabled BOOLEAN NOT NULL,
   authority TEXT NOT NULL
)
WITH (
   OIDS = FALSE
);

-- username:user passward:pass
INSERT INTO users VALUES (1, 'user', '$2a$10$sZD7w5TBbYX81BizHA4CTO8fpOXNP3vNxVJK7A2UY.Vzj60WB/NU.', TRUE, 'ROLE_USER');
-- username:admin passward:word
INSERT INTO users VALUES (2, 'admin', '$2a$10$4vlusNdHDMXY/fxDraVqcutoc4ryiwngZLS0ZLmRsDbIXfIXMUrRW', TRUE, 'ROLE_ADMIN');
