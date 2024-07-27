CREATE OR REPLACE PACKAGE complex_example_pack IS
    TYPE numbers IS
        TABLE OF NUMBER INDEX BY BINARY_INTEGER;
    TYPE address IS RECORD (
            district VARCHAR(255),
            city     VARCHAR(255),
            ages     numbers
    );
    TYPE address_array IS
        TABLE OF address;
    TYPE customer IS RECORD (
            birthday         DATE,
            original_address address,
            other_address    address_array
    );
    TYPE customer_array IS
        TABLE OF customer;
    PROCEDURE example_proc (
        in_customer   IN customer,
        in_customers  IN customer_array,
        out_customer  OUT customer,
        out_customers OUT customer_array
    );

END complex_example_pack;
/

CREATE OR REPLACE PACKAGE BODY complex_example_pack IS

    PROCEDURE example_proc (
        in_customer   IN customer,
        in_customers  IN customer_array,
        out_customer  OUT customer,
        out_customers OUT customer_array
    ) AS
    BEGIN
        out_customer := in_customer;
        out_customers := in_customers;
    END example_proc;

END complex_example_pack;