create extension if not exists pgcrypto;

create table Client(
    id varchar(20) not null constraint pk_client primary key ,
    active boolean not null default true,
    name varchar(100) not null,
    notes varchar(255),
    max_minutes integer,
    rate_in_cents_per_hour integer
);

create table Project(
    id bigserial not null constraint pk_project primary key,
    client_id varchar(20) not null constraint fk_project_client references Client(id),
    active boolean not null default true,
    name varchar(100) not null,
    max_minutes integer,
    rate_in_cents_per_hour integer,
    billable boolean not null default true
);

create table Reporting_User(
    username varchar(100) not null constraint pk_reporting_user primary key,
    password varchar(100) not null,
    email varchar(255) not null constraint uq_reporting_user_email unique,
    activated boolean not null default true,
    can_book boolean not null default true,
    can_view_money boolean not null default true,
    admin boolean not null default true
);

create table Client_User(
    username varchar(100) not null constraint fk_client_user_user references Reporting_User(username),
    client_id varchar(20) not null constraint fk_client_user_client references Client(id),
    constraint pk_client_user primary key(username, client_id)
);

create table Time_Entry(
    id bigserial not null constraint pk_time_entry primary key ,
    starting timestamp not null,
    ending timestamp,
    project_id bigint constraint fk_time_entry_project references Project(id),
    description varchar(200),
    username varchar(100) not null constraint fk_time_entry_user references Reporting_User(username),
    billable boolean not null default true,
    billed boolean not null default false
);

insert into Reporting_User(username, password, email, can_book, can_view_money, admin) values (
    'admin', crypt('admin', gen_salt('bf')), 'admin@test.ch', true, true, true
);
