create table claw_machine
(
    id     UUID primary key,
    name   varchar(20),
    number int
);

create table plush
(
    id            UUID primary key,
    clawMachineId uuid,
    name          varchar(20),
    number        int,

    foreign key (clawMachineId) references claw_machine (id)
)