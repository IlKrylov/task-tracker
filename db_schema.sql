-- CREATE DATABASE  tasktracker_db;
USE tasktracker_db;

-- CREATE TABLE users (

--   id BIGINT NOT NULL AUTO_INCREMENT,
--   created TIMESTAMP NOT NULL default(current_timestamp()),
--   updated TIMESTAMP NOT NULL default(current_timestamp()),
--   status VARCHAR(25) NOT NULL,
--     
--   user_name VARCHAR(100) NOT NULL UNIQUE,
--   first_name VARCHAR(100) NOT NULL,
--   last_name VARCHAR(100) NOT NULL,
--   email VARCHAR(255) NOT NULL UNIQUE,
--   password VARCHAR(255) NOT NULL,

--   PRIMARY KEY (id)
-- );


-- CREATE TABLE roles (

--   id BIGINT NOT NULL AUTO_INCREMENT,
--   created TIMESTAMP NOT NULL default(current_timestamp()),
--   updated TIMESTAMP NOT NULL default(current_timestamp()),
--   status VARCHAR(25) NOT NULL,

--   name VARCHAR(100) NOT NULL UNIQUE,

--   PRIMARY KEY (id)
-- );

-- CREATE TABLE users_roles (
--   user_id BIGINT NOT NULL,
--   role_id BIGINT NOT NULL,

--   PRIMARY KEY (user_id, role_id),
--   FOREIGN KEY (user_id) REFERENCES tasktracker_db.users(id),
--   FOREIGN KEY (role_id) REFERENCES tasktracker_db.roles(id)
-- );

-- CREATE TABLE projects (
--   id BIGINT NOT NULL AUTO_INCREMENT,
--   created TIMESTAMP NOT NULL default(current_timestamp()),
--   updated TIMESTAMP NOT NULL default(current_timestamp()),
--   status VARCHAR(25) NOT NULL,
--   
--   name VARCHAR(100) NOT NULL UNIQUE,
--   description VARCHAR(255) NOT NULL,
--   
--   PRIMARY KEY (id)
-- );

-- CREATE TABLE users_projects (
--   user_id BIGINT NOT NULL,
--   project_id BIGINT NOT NULL,

--   PRIMARY KEY (user_id, project_id),
--   FOREIGN KEY (user_id) REFERENCES tasktracker_db.users(id),
--   FOREIGN KEY (project_id) REFERENCES tasktracker_db.projects(id)
-- );

-- CREATE TABLE tasks(
--   id BIGINT NOT NULL AUTO_INCREMENT,
--   created TIMESTAMP NOT NULL default(current_timestamp()),
--   updated TIMESTAMP NOT NULL default(current_timestamp()),
--   status VARCHAR(25) NOT NULL,
--   
--   name VARCHAR(100) NOT NULL UNIQUE,
--   description VARCHAR(255) NOT NULL,
--   
--   user_id BIGINT,
--   project_id BIGINT NOT NULL,
--   
--   PRIMARY KEY (id),
--   FOREIGN KEY (user_id) REFERENCES tasktracker_db.users(id),
--   FOREIGN KEY (project_id) REFERENCES tasktracker_db.projects(id)
-- );

-- ALTER table tasktracker_db.tasks
-- DROP INDEX name


