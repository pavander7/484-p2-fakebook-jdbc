-- Query 1
-- -----------------------------------------------------------------------------------
-- GOALS: (A) The first name(s) with the most letters
--        (B) The first name(s) with the fewest letters
--        (C) The first name held by the most users
--        (D) The number of users whose first name is that identified in (C)

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 1a
-- GOALS: (A), (B)
CREATE OR REPLACE PROCEDURE query_1a (num_results IN NUMBER) AS
BEGIN
    SELECT  u.First_Name, 
            LENGTH(u.First_Name) AS Name_Length
    FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
    ORDER BY Name_Length DESC
    LIMIT num_results; -- FIXME: replace with a variable in java
END query_1a;
/

-- Query 1b
-- GOALS: (C), (D)
CREATE OR REPLACE PROCEDURE query_1b (num_results IN NUMBER) AS
BEGIN
    SELECT  u.First_Name, 
            COUNT(u.First_Name) AS Name_Count
    FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
    ORDER BY Name_Count DESC
    LIMIT num_results; -- FIXME: replace with a variable in java
END query_1b;
/
