create table T_BUSLINIE(
    BUSNR integer not null PRIMARY KEY
);

create table T_FAHRTSTRECKE(
    ID integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    NAME varchar(255) not null,
    BUSNR integer not null,
    FOREIGN KEY (BUSNR) REFERENCES T_BUSLINIE(BUSNR)
);

create table T_HALTESTELLE(
    ID integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    BEZEICHNUNG varchar(255) not null
);

create table T_FAHRPLAN(
    ID integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    NAME varchar(255) not null
);

create table T_HALTESTELLENZUORDNUNG(
    ID integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    FAHRTZEIT integer not null,
    NAECHSTE_HALTESTELLE integer not null,
    HALTESTELLEID integer not null,
    FAHRTSTRECKEID integer not null,
    FOREIGN KEY (HALTESTELLEID) REFERENCES T_HALTESTELLE(id),
    FOREIGN KEY (FAHRTSTRECKEID) REFERENCES T_FAHRTSTRECKE(id),
    FOREIGN KEY (NAECHSTE_HALTESTELLE) REFERENCES T_HALTESTELLE(id)
);

create table T_FAHRPLANZUORDNUNG(
    ID integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
    RICHTUNG boolean not null,
    STARTZEITPUNKT integer not null,
    FAHRTSTRECKEID integer not null,
    FAHRPLANID integer not null,
    FOREIGN KEY (FAHRTSTRECKEID) REFERENCES T_FAHRTSTRECKE(id), 
    FOREIGN KEY (FAHRPLANID) REFERENCES T_FAHRPLAN(id)
);