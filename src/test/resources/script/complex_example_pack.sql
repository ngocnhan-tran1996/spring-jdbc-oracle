CREATE OR REPLACE PACKAGE complex_example_pack IS
    TYPE numbers IS
        TABLE OF NUMBER INDEX BY BINARY_INTEGER;
    TYPE address IS RECORD (
            district  VARCHAR(255),
            city      VARCHAR(255),
            my_values numbers
    );
    TYPE addresses IS
        TABLE OF address;
    TYPE customer IS RECORD (
            first_name       VARCHAR(255),
            last_name        VARCHAR(255),
            age              NUMBER,
            birthday         DATE,
            original_address address,
            other_addresses  addresses
    );
    TYPE customers IS
        TABLE OF customer;
    PROCEDURE example_proc (
        in_customer   IN customer,
        in_customers  IN customers,
        out_customer  OUT customer,
        out_customers OUT customers
    );

END complex_example_pack;
/

CREATE OR REPLACE PACKAGE BODY complex_example_pack IS

    PROCEDURE example_proc (
        in_customer   IN customer,
        in_customers  IN customers,
        out_customer  OUT customer,
        out_customers OUT customers
    ) AS
    BEGIN
        out_customer := in_customer;
        out_customers := in_customers;
    END example_proc;

END complex_example_pack;