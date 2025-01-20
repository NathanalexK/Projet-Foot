create table match (
    id serial primary key,
    date_match date default now()
);

create table equipe (
    id serial primary key,
    nom varchar(50)
);

create table equipe_marquant(
    id serial primary key,
    id_match int references match(id),
    id_equipe int references equipe(id)
);




insert into equipe values (1, 'bleu');
insert into equipe values (2, 'rouge');


insert into match values ('') returning id;


select
    count()
from equipe_marquant
group by id_match


create table jeu (
    id serial primary key,
    id_set int references set(id),
    type_jeu int,
);

create table terrain (
    id serial primary key,
    longueur numeric(15, 2),
    largeur numeric(15, 2)
);

create table joueur (
    id serial primary key,
)

create or replace view v_score_match as;
select


create table score (
    id int primary key,
    nom_equipe varchar(50),
    point int
);

select
    *
from equipe_marquant
where
