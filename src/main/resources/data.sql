insert into T_BUSLINIE(BUSNR)
VALUES (2004);
insert into T_BUSLINIE(BUSNR)
VALUES (2006);
insert into T_BUSLINIE(BUSNR)
VALUES (2010);
insert into T_BUSLINIE(BUSNR)
VALUES (2012);

insert into T_HALTESTELLE(NAME)
VALUES ('Schule');
insert into T_HALTESTELLE(NAME)
VALUES ('Bahnhof');
insert into T_HALTESTELLE(NAME)
VALUES ('Tierarzt');
insert into T_HALTESTELLE(NAME)
VALUES ('Zoo');
insert into T_HALTESTELLE(NAME)
VALUES ('Markt');
insert into T_HALTESTELLE(NAME)
VALUES ('Rathaus');
insert into T_HALTESTELLE(NAME)
VALUES ('Stadtmitte');
insert into T_HALTESTELLE(NAME)
VALUES ('Polizei');
insert into T_HALTESTELLE(NAME)
VALUES ('Turm');
insert into T_FAHRPLAN(NAME)
VALUES ('Fahrplan 1');
insert into T_FAHRPLAN(NAME)
VALUES ('Fahrplan Schulzeit');

insert into T_FAHRTSTRECKE(NAME, BUSLINIE)
VALUES ('Stadtrundfahrt', 1);
insert into T_FAHRTSTRECKE(NAME, BUSLINIE)
VALUES ('Schulbus', 2);
insert into T_FAHRTSTRECKE(NAME, BUSLINIE)
VALUES ('Kamelritt', 1);
insert into T_FAHRTSTRECKE(NAME, BUSLINIE)
VALUES ('Trödelbahn', 3);
insert into T_FAHRTSTRECKE(NAME, BUSLINIE)
VALUES ('Sehenswürdigkeiten', 2);
insert into T_FAHRTSTRECKE(NAME, BUSLINIE)
VALUES ('Arbeitsweg', 2);

insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (1, 2, 3, 7);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (1, 1, 2, 10);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (2, 9, 3, 9);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (2, 3, 5, 8);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (2, 5, 4, 3);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (2, 4, 1, 7);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (2, 1, 8, 12);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (3, 4, 7, 5);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (3, 7, 2, 10);

insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (4, 1, 3, 6);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (4, 3, 5, 12);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (4, 5, 2, 3);
insert into T_HALTESTELLENZUORDNUNG(
        FAHRTSTRECKE,
        HALTESTELLE,
        NAECHSTE_HALTESTELLE,
        FAHRTZEIT
    )
VALUES (4, 2, 8, 9);

insert into T_FAHRPLANZUORDNUNG(
        FAHRPLAN,
        FAHRTSTRECKE,
        STARTZEITPUNKT,
        RICHTUNG
    )
VALUES (1, 1, '10:00:00', true);
insert into T_FAHRPLANZUORDNUNG(
        FAHRPLAN,
        FAHRTSTRECKE,
        STARTZEITPUNKT,
        RICHTUNG
    )
VALUES (1, 1, '12:00:00', false);
insert into T_FAHRPLANZUORDNUNG(
        FAHRPLAN,
        FAHRTSTRECKE,
        STARTZEITPUNKT,
        RICHTUNG
    )
VALUES (1, 1, '14:30:00', true);
insert into T_FAHRPLANZUORDNUNG(
        FAHRPLAN,
        FAHRTSTRECKE,
        STARTZEITPUNKT,
        RICHTUNG
    )
VALUES (1, 3, '09:00:00', true);
