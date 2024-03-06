-- DROP TYPE public."gender";

CREATE TYPE public."gender" AS ENUM (
	'male',
	'female',
	'other');

-- DROP TYPE public."userstatus";

CREATE TYPE public."userstatus" AS ENUM (
	'active',
	'inactive',
	'none');

-- DROP TYPE public."usertype";

CREATE TYPE public."usertype" AS ENUM (
	'sysadmin',
	'admin',
	'manager',
	'user');

-- public.tbl_user definition

-- Drop table

-- DROP TABLE public.tbl_user;

CREATE TABLE public.tbl_user (
                                 created_at timestamp(6) NULL,
                                 id bigserial NOT NULL,
                                 updated_at timestamp(6) NULL,
                                 date_of_birth varchar(255) NULL,
                                 email varchar(255) NULL,
                                 first_name varchar(255) NULL,
                                 last_name varchar(255) NULL,
                                 "password" varchar(255) NULL,
                                 phone varchar(255) NULL,
                                 username varchar(255) NULL,
                                 "gender" public."gender" NULL,
                                 status public."userstatus" NULL,
                                 "type" public."usertype" NULL,
                                 CONSTRAINT tbl_user_pkey PRIMARY KEY (id)
);

-- public.tbl_address definition

-- Drop table

-- DROP TABLE public.tbl_address;

CREATE TABLE public.tbl_address (
                                    address_type int4 NULL,
                                    created_at timestamp(6) NULL,
                                    id bigserial NOT NULL,
                                    updated_at timestamp(6) NULL,
                                    user_id int8 NULL,
                                    apartment_number varchar(255) NULL,
                                    building varchar(255) NULL,
                                    city varchar(255) NULL,
                                    country varchar(255) NULL,
                                    floor varchar(255) NULL,
                                    street varchar(255) NULL,
                                    street_number varchar(255) NULL,
                                    CONSTRAINT tbl_address_pkey PRIMARY KEY (id)
);


-- public.tbl_address foreign keys

ALTER TABLE public.tbl_address ADD CONSTRAINT fklo13i087wmqhi0h7ffjxoljrb FOREIGN KEY (user_id) REFERENCES public.tbl_user(id);