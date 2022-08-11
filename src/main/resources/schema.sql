create table if not exists snp500 (
    createTime timestamp default current_timestamp not null,
    lastUpdateTime timestamp default current_timestamp on update current_timestamp not null,
    state varchar(20) not null,
    symbol varchar(20) not null,
    security varchar(80) not null,
    GICSSector varchar(80) not null,
    GICSSubIndustry varchar(80) not null,
    primary key (createTime, symbol)
);

create table if not exists portfolio (
    id varchar(40) not null primary key,
    timeFrom timestamp not null,
    timeTo timestamp not null
);

create table if not exists investment (
    id varchar(40) not null,
    symbol varchar(20) not null,
    allocation double not null,
    foreign key(id) references portfolio(id)
)