-- springboot起動時に呼び出されるDML
-- username:user passward:pass
INSERT INTO users(id, username, password, enabled, authority) VALUES (1, 'user', '$2a$10$sZD7w5TBbYX81BizHA4CTO8fpOXNP3vNxVJK7A2UY.Vzj60WB/NU.', TRUE, 'ROLE_USER');
-- username:admin passward:word
INSERT INTO users(id, username, password, enabled, authority) VALUES (2, 'admin', '$2a$10$4vlusNdHDMXY/fxDraVqcutoc4ryiwngZLS0ZLmRsDbIXfIXMUrRW', TRUE, 'ROLE_ADMIN');
