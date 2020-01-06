alter table project
drop constraint fk_project_client,
add constraint fk_project_client foreign key(client_id) references client(id) on delete cascade on update cascade;

alter table Client_User
drop constraint fk_client_user_user,
drop constraint fk_client_user_client,
add constraint fk_client_user_user foreign key(email) references reporting_user(email) on delete cascade on update cascade,
add constraint fk_client_user_client foreign key(client_id) references client(id) on delete cascade on update cascade;

alter table time_entry
drop constraint fk_time_entry_project,
add constraint fk_time_entry_project foreign key(project_id) references project(id) on delete set null on update cascade,
drop constraint fk_time_entry_user,
add constraint fk_time_entry_user foreign key(email) references reporting_user(email) on delete set null on update cascade;
