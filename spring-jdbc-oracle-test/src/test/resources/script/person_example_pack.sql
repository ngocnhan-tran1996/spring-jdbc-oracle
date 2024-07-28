CREATE OR REPLACE TYPE person_obj AS OBJECT (
        first_name VARCHAR(255),
        last_name  VARCHAR(255),
        age        NUMBER,
        birthdate  DATE
);
/

CREATE OR REPLACE PACKAGE person_example_pack IS
    TYPE persons IS
        TABLE OF person_obj;
    PROCEDURE example_proc (
        in_person   IN person_obj,
        in_persons  IN persons,
        out_person  OUT person_obj,
        out_persons OUT persons
    );

END person_example_pack;
/

CREATE OR REPLACE PACKAGE BODY person_example_pack IS

    PROCEDURE example_proc (
        in_person   IN person_obj,
        in_persons  IN persons,
        out_person  OUT person_obj,
        out_persons OUT persons
    ) AS
    BEGIN
        out_person := in_person;
        out_persons := in_persons;
    END example_proc;

END person_example_pack;