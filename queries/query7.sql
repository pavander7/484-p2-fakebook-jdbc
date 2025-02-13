-- Query 7
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the name of the state or states in which the most events are held
--        (B) Find the number of events held in the states identified in (A)

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 7a
-- Goals: (A), (B)
CREATE OR REPLACE PROCEDURE query_7a (num_results IN NUMBER) AS
BEGIN
    SELECT  c.state_name, 
            c.country_name, 
            COUNT(e.event_id) AS Num_Events
    FROM project2.Public_Cities c -- FakebookOracleConstants.CitiesTable c
    JOIN project2.Public_User_Events e -- FakebookOracleConstants.EventsTable e
        ON c.city_id = e.event_city_id
    GROUP BY (c.state_name, c.country_name)
    ORDER BY Num_Events, c.state_name, c.country_name
    LIMIT num_results; -- FIXME: replace with a variable in java
END query_7a;
/