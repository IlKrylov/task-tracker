DELETE FROM users_projects;
DELETE FROM users_roles WHERE user_id NOT IN ( 1 );
DELETE FROM tasks;
DELETE FROM projects;
DELETE FROM users WHERE id NOT IN ( 1 );

INSERT INTO projects (id, created, updated, status, name, description) VALUES
(1,'2021-10-26 13:53:48','2021-10-26 13:53:48','ACTIVE','Project1','TestProject1'),
(2,'2021-10-26 13:53:48','2021-10-26 13:53:48','ACTIVE','Project2','TestProject2'),
(3,'2021-10-26 13:53:48','2021-10-26 13:53:48','ACTIVE','Project3','TestProject3'),
(4,'2021-10-26 13:53:48','2021-10-26 13:53:48','ACTIVE','Project4','TestProject4'),
(5,'2021-11-03 10:56:09','2021-11-03 10:58:43','ACTIVE','Project5','TestProject5');

INSERT INTO users (id, created, updated, status, user_name, first_name, last_name, email, password) VALUES
-- (1,'2021-10-24 16:19:14','2021-10-24 16:19:14','ACTIVE','ADMIN','ADMIN','ADMIN','admin@admin.ru','$2a$10$Ht1HSQHjLfF/jcWe.L35zuZYhvPS2N7.A.8fk94C/TyLYSBeSI1Ey'),
(2,'2021-10-24 16:19:14','2021-10-24 16:19:14','ACTIVE','User2','User2','User2','user2@user.ru','$2a$10$Ht1HSQHjLfF/jcWe.L35zuZYhvPS2N7.A.8fk94C/TyLYSBeSI1Ey'),
(3,'2021-10-24 16:19:14','2021-10-24 16:19:14','ACTIVE','User3','User3','User3','user3@user.ru','$2a$10$Ht1HSQHjLfF/jcWe.L35zuZYhvPS2N7.A.8fk94C/TyLYSBeSI1Ey'),
(4,'2021-10-24 16:19:14','2021-10-24 16:19:14','ACTIVE','User4','User4','User4','user4@user.ru','$2a$10$Ht1HSQHjLfF/jcWe.L35zuZYhvPS2N7.A.8fk94C/TyLYSBeSI1Ey');

INSERT INTO users_projects (user_id, project_id) VALUES
(1,1), (1,2), (1,3),
(2,1), (2,2), (2,3), (2,4),(2,5),
(3,1);

INSERT INTO users_roles (user_id, role_id) VALUES
(2,2), (3,2), (4,2);


INSERT INTO tasks (id, created, updated, status, name, description, user_id, project_id) VALUES
-- ADMIN
(1,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task1','Task1 description',1,1),
(2,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task2','Task2 description',1,2),
(3,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task3','Task3 description',1,2),
(4,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task4','Task4 description',1,3),
(5,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task5','Task5 description',1,3),
(6,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task6','Task6 description',1,3),
-- USER2
(7,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task7','Task7 description',2,2),
(8,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task8','Task8 description',2,2),
(9,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task9','Task9 description',2,3),
(10,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task10','Task10 description',2,3),
(11,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task11','Task11 description',2,3),
(12,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task12','Task12 description',2,4),
(13,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task13','Task13 description',2,4),
(14,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task14','Task14 description',2,4),
(15,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task15','Task15 description',2,4),
(16,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task16','Task16 description',2,5),
(17,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task17','Task17 description',2,5),
(18,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task18','Task18 description',2,5),
(19,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task19','Task19 description',2,5),
(20,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task20','Task20 description',2,5),
-- USER3
(21,'2021-10-27 08:57:08','2021-10-27 08:57:08','ACTIVE','Task 21','Task21 description',3,1);
