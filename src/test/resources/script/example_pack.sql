CREATE OR REPLACE PACKAGE example_pack IS
    TYPE customer IS RECORD (
            first_name VARCHAR(255),
            last_name  VARCHAR(255),
            age        NUMBER
    );
    TYPE customers IS
        TABLE OF customer;
    TYPE numbers IS
        TABLE OF NUMBER INDEX BY BINARY_INTEGER;
    PROCEDURE example_proc (
        in_numbers       IN numbers,
        in_customer      IN customer,
        in_customers     IN customers,
        in_out_numbers   IN OUT numbers,
        in_out_customer  IN OUT customer,
        in_out_customers IN OUT customers,
        out_numbers      OUT numbers,
        out_customer     OUT customer,
        out_customers    OUT customers
    );

END example_pack;
/
CREATE OR REPLACE PACKAGE BODY example_pack IS

    PROCEDURE example_proc (
        in_numbers       IN numbers,
        in_customer      IN customer,
        in_customers     IN customers,
        in_out_numbers   IN OUT numbers,
        in_out_customer  IN OUT customer,
        in_out_customers IN OUT customers,
        out_numbers      OUT numbers,
        out_customer     OUT customer,
        out_customers    OUT customers
    ) AS
    BEGIN
        in_out_numbers := in_numbers;
        in_out_customer := in_customer;
        in_out_customers := in_customers;
        out_numbers := in_numbers;
        out_customer := in_customer;
        out_customers := in_customers;
    END example_proc;

END example_pack;