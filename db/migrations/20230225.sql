create table tag
(
    id   bigint auto_increment,
    name varchar(50) not null,
    constraint tag_pk
        primary key (id)
);

create unique index tag_name_uindex
    on tag (name);

create table ting_tag
(
    id      bigint auto_increment,
    ting_id bigint not null,
    tag_id  bigint not null,
    constraint ting_tag_pk
        primary key (id)
);

create index ting_tag_tag_id_index
    on ting_tag (tag_id);

create index ting_tag_ting_id_index
    on ting_tag (ting_id);
