-- Query 6
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the IDs, first names, and last names of each of the two users in
--            the top <num> pairs of users who are not friends but have a lot of
--            common friends
--        (B) For each pair identified in (A), find the IDs, first names, and last names
--            of all the two users' common friends

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 6a
-- Goals: (A)
SELECT  u1.user_id, 
        u1.first_name, 
        u1.last_name, 
        u2.user_id, 
        u2.first_name, 
        u2.last_name, 
        COUNT(*) AS Mutual_Count
-- select ALL pairs of users
FROM project2.Public_Users u1 -- FakebookOracleConstants.UsersTable u1
JOIN project2.Public_Users u2 -- FakebookOracleConstants.UsersTable u2
    ON u1.user_id < u2.user_id
-- (i) find pairs of friends in common
-- (i)(a) find friends of u1 that are not u2
JOIN project2.Public_Friends f1 -- FakebookOracleConstants.FriendsTable f1
    ON (
        (u1.user_id = f1.user1_id AND u2.user_id <> f1.user2_id) 
        OR 
        (u1.user_id = f1.user2_id AND u2.user_id <> f1.user1_id)
    )
-- (i)(b) select only friends of u1 that are friends of u2
WHERE EXISTS (
    SELECT 1
    FROM project2.Public_Friends f2 -- FakebookOracleConstants.FriendsTable f2
    WHERE (
        (
            f2.user1_id = u2.user_id 
            AND 
            (
                (f1.user1_id = f2.user2_id AND f1.user1_id <> u1.user_id)
                OR
                (f1.user2_id = f2.user2_id AND f1.user2_id <> u1.user_id)
            )
        )
        OR
        (
            f2.user2_id = u2.user_id 
            AND 
            (
                (f1.user1_id = f2.user1_id AND f1.user1_id <> u1.user_id)
                OR
                (f1.user2_id = f2.user1_id AND f1.user2_id <> u1.user_id)
            )
        )
    )
)
-- ensures no duplicates or self-pairs
AND u1.user_id < u2.user_id
-- (ii) ensure users are not already friends
AND NOT EXISTS (
    SELECT 1
    FROM project2.Public_Friends f3 -- FakebookOracleConstants.FriendsTable f3
    WHERE f3.user1_id = u1.user_id
    AND f3.user2_id = u2.user_id
    -- u1.user_id < u2.user_id and f3.user1_id < f3.user2_id -> 
    -- no need to check if f3.user2_id = u1.user_id and f3.user1_id = u2.user_id
) 
GROUP BY u1.user_id, 
         u1.first_name, 
         u1.last_name, 
         u2.user_id, 
         u2.first_name, 
         u2.last_name
ORDER BY Mutual_Count DESC, u1.user_id ASC, u2.user_id ASC
FETCH FIRST 4 ROWS ONLY; -- FIXME: replace with a variable in java

-- Query 6b
-- Goals: (B)
SELECT  u.user_id, 
        u.first_name, 
        u.last_name
FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
WHERE EXISTS (
    SELECT 1
    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f
    WHERE (
        (f.user1_id = u.user_id AND f.user2_id = 42) -- FIXME: replace with a variable in java
        OR 
        (f.user2_id = u.user_id AND f.user1_id = 42) -- FIXME: replace with a variable in java
    )
) 
AND EXISTS (
    SELECT 1
    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f
    WHERE (
        (f.user1_id = u.user_id AND f.user2_id = 318) -- FIXME: replace with a variable in java
        OR 
        (f.user2_id = u.user_id AND f.user1_id = 318) -- FIXME: replace with a variable in java
    )
)
ORDER BY u.user_id;