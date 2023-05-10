create table claw_machine
(
    id     UUID primary key,
    name   varchar(20) not null,
    number int
);

create table plush
(
    id                  UUID primary key,
    claw_machine_id     uuid                   not null,
    name                varchar(20)            not null,
    image_absolute_path varchar(255)           not null,
    number              int,
    state               enum ('FREE', 'TAKEN') not null,

    foreign key (claw_machine_id) references claw_machine (id) ON DELETE CASCADE
);

create table plush_locker
(
    id          UUID primary key,
    plush_id    uuid                     not null,
    name        varchar(20)              not null,
    lock_date   TIMESTAMP WITH TIME ZONE not null,
    unlock_date TIMESTAMP WITH TIME ZONE,

    foreign key (plush_id) references plush (id) ON DELETE CASCADE
);
