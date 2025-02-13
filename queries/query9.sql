-- Query 9
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find all pairs of users that meet each of the following criteria
--              (i) same last name
--              (ii) same hometown
--              (iii) are friends
--              (iv) less than 10 birth years apart

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 9a
-- Goals: (A)
SELECT  u1.user_id, 
        u2.user_id
FROM project2.Public_Users u1 -- FakebookOracleConstants.UsersTable u1
-- (i) same last name
JOIN project2.Public_Users u2 -- FakebookOracleConstants.UsersTable u2
    ON u1.last_name = u2.last_name
-- (ii) same hometown
WHERE EXISTS (
    SELECT 1
    FROM project2.Public_User_Hometown_Cities h1 -- FakebookOracleConstants.HometownCitiesTable h1
    JOIN project2.Public_User_Hometown_Cities h2 -- FakebookOracleConstants.HometownCitiesTable h2
        ON h1.user_id = h2.user_id
    WHERE h1.user_id = u1.user_id
    AND h2.user_id = u2.user_id
)
-- (iii) are friends
AND EXISTS (
    SELECT 1
    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f
    WHERE f.user1_id = u1.user_id
    AND f.user2_id = u2.user_id
)
-- (iv) less than 10 birth years apart
AND ABS(u1.year_of_birth - u2.year_of_birth) < 10
-- ensures no duplicates or self-pairs
AND u1.user_id < u2.user_id
ORDER BY u1.user_id, u2.user_id;