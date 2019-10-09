create table ClientBudgets (
    client int not null,
    clientName varchar(100) not null,
    budget decimal(20, 2) not null
);

create table ProjectBudgets (
    client int not null,
    projectId int not null,
    projectName varchar(100),
    budget decimal(20, 2) not null
)
