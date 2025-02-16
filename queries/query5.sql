-- Query 5
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
--            users in the top <num> pairs of users that meet each of the following
--            criteria:
--              (i) same gender
--              (ii) tagged in at least one common photo
--              (iii) difference in birth years is no more than <yearDiff>
--              (iv) not friends
--        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
--            the containing album of each photo in which they are tagged together

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 5a
-- Goals: (A)
SELECT  u1.user_id, 
        u1.first_name, 
        u1.last_name, 
        u1.year_of_birth, 
        u2.user_id, 
        u2.first_name, 
        u2.last_name, 
        u2.year_of_birth
FROM project2.Public_Users u1 -- FakebookOracleConstants.UsersTable u1
JOIN project2.Public_Users u2 -- FakebookOracleConstants.UsersTable u2
-- (i) same gender
    ON u1.gender = u2.gender 
-- ensures users are different and that pairs are only selected once
WHERE u1.user_id < u2.user_id
-- (ii) tagged in at least one common photo
AND EXISTS (
    SELECT 1
    FROM project2.Public_Tags t1 -- PublicFakebookConstants.TagsTable t1
    JOIN project2.Public_Tags t2 -- PublicFakebookConstants.TagsTable t2
        ON t1.tag_photo_id = t2.tag_photo_id
    WHERE t1.tag_subject_id = u1.user_id
    AND t2.tag_subject_id = u2.user_id
) 
-- (iii) difference in birth years is no more than <yearDiff>
AND ABS(u1.year_of_birth - u2.year_of_birth) <= 2 -- FIXME: replace with a variable in java
-- (iv) not friends
AND NOT EXISTS (
    SELECT 1
    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f
    WHERE f.user1_id = u1.user_id
    AND f.user2_id = u2.user_id
    -- u1.user_id < u2.user_id and f.user1_id < f.user2_id -> 
    -- no need to check if f.user2_id = u1.user_id and f.user1_id = u2.user_id
)
ORDER BY u1.user_id, u2.user_id
FETCH FIRST 2 ROWS ONLY; -- FIXME: replace with a variable in java

-- Query 5b
-- Goals: (B)
SELECT  p.photo_id, 
        p.photo_link, 
        a.album_id, 
        a.album_name
FROM project2.Public_Photos p -- PublicFakebookConstants.PhotosTable p
JOIN project2.Public_Albums a -- PublicFakebookConstants.AlbumsTable a
    ON p.album_id = a.album_id
WHERE EXISTS(
    SELECT 1
    FROM project2.Public_Tags t -- PublicFakebookConstants.TagsTable t
    WHERE t.tag_subject_id = 243 -- FIXME: replace with a variable in java
    AND t.tag_photo_id = p.photo_id
) 
AND EXISTS(
    SELECT 1
    FROM project2.Public_Tags t -- PublicFakebookConstants.TagsTable t
    WHERE t.tag_subject_id = 524 -- FIXME: replace with a variable in java
    AND t.tag_photo_id = p.photo_id
) 
ORDER BY p.photo_id;