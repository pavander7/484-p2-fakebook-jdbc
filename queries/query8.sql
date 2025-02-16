-- Query 8
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
--            with User ID <userID>
--        (B) Find the ID, first name, and last name of the youngest friend of the user
--            with User ID <userID>

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 8a
-- Goals: (A), (B)
SELECT  u.user_id, 
        u.first_name, 
        u.last_name, 
        u.year_of_birth, 
        u.month_of_birth, 
        u.day_of_birth
FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
JOIN project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f
    ON (
        (f.user1_id = u.user_id AND f.user2_id = 426) -- FIXME: replace with a variable in java
        OR 
        (f.user2_id = u.user_id AND f.user1_id = 426) -- FIXME: replace with a variable in java
    )
ORDER BY u.year_of_birth ASC, u.month_of_birth ASC, u.day_of_birth ASC, u.user_id DESC;