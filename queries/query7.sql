-- Query 7
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the name of the state or states in which the most events are held
--        (B) Find the number of events held in the states identified in (A)

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 7a
-- Goals: (A), (B)
SELECT c.state_name, 
        c.country_name, 
        COUNT(e.event_id) AS Num_Events
FROM project2.Public_Cities c -- FakebookOracleConstants.CitiesTable c
JOIN project2.Public_User_Events e -- FakebookOracleConstants.EventsTable e
ON c.city_id = e.event_city_id
GROUP BY c.state_name, c.country_name
ORDER BY Num_Events DESC, c.state_name ASC, c.country_name ASC;