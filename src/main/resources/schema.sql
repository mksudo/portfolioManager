drop table if exists snp500;

create table snp500 (
    createTime timestamp default current_timestamp,
    lastUpdateTime timestamp default current_timestamp on update current_timestamp,
    state varchar(20),
    symbol varchar(20),
    security varchar(80),
    GICSSector varchar(80),
    GICSSubIndustry varchar(80),
    primary key (createTime, symbol)
);