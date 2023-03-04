insert into user (id, created_at, encrypted_password, name, email, verified) values (1, CURRENT_TIMESTAMP(6), '$2a$10$wJ0g3MsN5M/UogWIXkZHKuF.28jEsxJTuI61TXWxmng8T68FP2OYe', 'System', '', 1);
insert into program (id, created_at, created_by, description, language, title, updated_at, visible) values (1, CURRENT_TIMESTAMP(6), 1, 'NHK NEWS WEB EASY 是 NHK 下面向小学生、中学生，以及在日本居住的外国人的简单新闻栏目。', 2, 'NHK NEWS WEB EASY', CURRENT_TIMESTAMP(6), 1);
insert into program (id, created_at, created_by, description, language, title, updated_at, visible) values (2, CURRENT_TIMESTAMP(6), 1, 'VOA 慢速英语，语速比标准 VOA 慢三分之一。', 1, 'VOA 慢速英语', CURRENT_TIMESTAMP(6), 1);
insert into tag (id, name) values (1, 'Arts & Culture');
insert into tag (id, name) values (2, 'Education');
insert into tag (id, name) values (3, 'Health & Lifestyle');
insert into tag (id, name) values (4, 'Science & Technology');
