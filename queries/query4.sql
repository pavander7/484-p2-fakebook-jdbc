-- Query 4
-- -----------------------------------------------------------------------------------
-- GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
--            <num> photos with the most tagged users
--        (B) For each photo identified in (A), find the IDs, first names, and last names
--            of the users therein tagged

-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA
-- WARNING: TABLE NAMES MUST BE REPLACED WITH CONSTANT VARIABLES WHEN COPIED TO JAVA

-- Query 4a
-- Goals: (A)
SELECT  p.photo_id, 
        p.photo_link, 
        a.album_id, 
        a.album_name, 
        COUNT(DISTINCT t.tag_subject_id) AS Num_Tags
FROM project2.Public_Photos p -- PublicFakebookConstants.PhotosTable p
JOIN project2.Public_Albums a -- PublicFakebookConstants.AlbumsTable a
    ON p.album_id = a.album_id
LEFT JOIN project2.Public_Tags t -- PublicFakebookConstants.TagsTable t
    ON p.photo_id = t.tag_photo_id
GROUP BY p.photo_id, p.photo_link, a.album_id, a.album_name
ORDER BY Num_Tags DESC, p.photo_id ASC
FETCH FIRST 5 ROWS ONLY; -- FIXME: replace with a variable in java

-- Query 4b
-- Goals: (B)
SELECT  u.user_id, 
        u.first_name, 
        u.last_name
FROM project2.Public_Photos p -- PublicFakebookConstants.PhotosTable p
JOIN project2.Public_Tags t -- PublicFakebookConstants.TagsTable t
    ON p.photo_id = t.tag_photo_id
JOIN project2.Public_Users u -- FakebookOracleConstants.UsersTable u
    ON t.tag_subject_id = u.user_id
WHERE p.photo_id = 54 -- FIXME: replace with a variable in java
ORDER BY u.user_id;