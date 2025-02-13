-- Query 3
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the IDs, first names, and last names of users who no longer live
--            in their hometown (i.e. their current city and their hometown are different)

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 3a
-- Goals: (A)
SELECT  u.User_ID, 
        u.First_Name, 
        u.Last_Name
FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
JOIN project2.Public_User_Current_Cities c -- FakebookOracleConstants.CurrentCitiesTable c
    ON u.User_ID = c.User_ID
JOIN project2.Public_User_Hometown_Cities h -- FakebookOracleConstants.HometownCitiesTable h
    ON u.User_ID = h.User_ID
WHERE c.Current_City_ID <> h.Hometown_City_ID
ORDER BY u.User_ID;