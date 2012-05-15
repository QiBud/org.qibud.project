# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bud_entity (
  bud_identity              varchar(255) not null,
  title                     varchar(512),
  posted_at                 timestamp,
  content                   varchar(30000),
  has_attachment            boolean,
  constraint pk_bud_entity primary key (bud_identity))
;

create sequence bud_entity_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists bud_entity;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists bud_entity_seq;

