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
SELECT  DISTINCT u.First_Name, 
        LENGTH(u.First_Name) AS Name_Length
FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u
ORDER BY Name_Length DESC, u.First_Name;

-- Query 1b
-- GOALS: (C), (D)
WITH ordered AS (
  SELECT DISTINCT u.First_Name,
         COUNT(u.First_Name) AS Name_Count
  FROM project2.Public_Users u
  GROUP BY u.first_name
  ORDER BY Name_Count DESC, u.First_Name
),
boundary AS (
  SELECT Name_Count AS tenth_name_count
  FROM ordered
  FETCH NEXT 1 ROW ONLY
)
SELECT o.First_Name, o.Name_Count
FROM ordered o, boundary b
WHERE o.Name_Count >= b.tenth_name_count
ORDER BY o.Name_Count DESC, o.First_Name;
