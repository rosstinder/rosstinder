create table if not exists rosstinder.users (
  chat_id           bigint NOT NULL primary key,
  status            varchar[30],
  last_favorite_num bigint,
  last_profile_num  bigint
);

create table if not exists rosstinder.profiles (
  chat_id           bigint NOT NULL primary key,
  name              varchar[50],
  gender            varchar[10],
  description       varchar[650],
  preference        varchar[10]
);

create table if not exists rosstinder.favorites (
  id            bigint NOT NULL primary key,
  who           bigint NOT NULL,
  whom          bigint NOT NULL,
  is_like       bool
);

