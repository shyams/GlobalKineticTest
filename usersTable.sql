CREATE TABLE users
(
  id SERIAL,
  username character varying(50) NOT NULL,
  password character varying(50) NOT NULL,
  phone character varying(50) NOT NULL,
  status character varying(2) NOT NULL,
  key character varying(150),
  logintime character varying(150),
  CONSTRAINT "users_key" PRIMARY KEY (id)
)
