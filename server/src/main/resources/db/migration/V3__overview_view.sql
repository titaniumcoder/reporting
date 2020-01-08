drop view if exists client_overview;

create view client_overview
as
select c.id                     as client_id,
       c.name                   as client_name,
       c.rate_in_cents_per_hour as client_rate_in_cents_per_hour,
       c.max_minutes            as client_max_minutes,
       p.id                     as project_id,
       p.name                   as project_name,
       p.rate_in_cents_per_hour as project_rate_in_cents_per_hour,
       p.max_minutes            as project_max_minutes,
       (select EXTRACT(EPOCH FROM sum(t.ending - t.starting)) / 60
        from time_entry t
        where t.project_id = p.id
          and billed = true
          and billable = true)  as project_billed_minutes,
       (select EXTRACT(EPOCH FROM sum(t.ending - t.starting)) / 60
        from time_entry t
        where t.project_id = p.id
          and billed = false
          and billable = true)  as project_open_minutes
from client c
         left join project p on c.id = p.client_id
order by c.id, p.name;
