-- Query 2
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the IDs, first names, and last names of users without any friends
--
-- Be careful! Remember that if two users are friends, the Friends table only contains
-- the one entry (U1, U2) where U1 < U2.

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 2a
-- Goals: (A)
SELECT  u.User_ID, 
        u.First_Name, 
        u.Last_Name
FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
WHERE NOT EXISTS (
    SELECT 1
    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f
    WHERE u.User_ID = f.User1_ID
    OR u.User_ID = f.User2_ID
) 
ORDER BY u.User_ID;