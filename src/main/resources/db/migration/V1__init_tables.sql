CREATE TABLE IF NOT EXISTS brand
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(30),
    country VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS motorcycle
(
    id SERIAL PRIMARY KEY,
    brand_id INT,
    model VARCHAR(50),
    FOREIGN KEY (brand_id) REFERENCES brand(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS role
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS usr
(
    id SERIAL PRIMARY KEY,
    role_id INT,
    username VARCHAR(20),
    password CHAR(60),
    public_name VARCHAR(30),
    FOREIGN KEY (role_id) REFERENCES role(id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS review
(
    id BIGSERIAL PRIMARY KEY,
    user_id INT,
    motorcycle_id INT,
    body VARCHAR(1000),
    FOREIGN KEY (user_id) REFERENCES usr(id) ON UPDATE CASCADE,
    FOREIGN KEY (motorcycle_id) REFERENCES motorcycle(id) ON UPDATE CASCADE ON DELETE CASCADE
);