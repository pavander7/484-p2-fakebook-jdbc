package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }

    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                    "SELECT COUNT(*) AS Birthed, Month_of_Birth " + // select birth months and number of users with that birth month
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth IS NOT NULL " + // for which a birth month is available
                            "GROUP BY Month_of_Birth " + // group into buckets by birth month
                            "ORDER BY Birthed DESC, Month_of_Birth ASC"); // sort by users born in that month, descending; break ties by birth month

            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    mostMonth = rst.getInt(2); //   it is the month with the most
                }
                if (rst.isLast()) { // if last record
                    leastMonth = rst.getInt(2); //   it is the month with the least
                }
                total += rst.getInt(1); // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);

            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + mostMonth + " " + // born in the most popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + leastMonth + " " + // born in the least popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }

    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                FirstNameInfo info = new FirstNameInfo();
                info.addLongName("Aristophanes");
                info.addLongName("Michelangelo");
                info.addLongName("Peisistratos");
                info.addShortName("Bob");
                info.addShortName("Sue");
                info.addCommonName("Harold");
                info.addCommonName("Jessica");
                info.setCommonNameCount(42);
                return info;
            */

            // Step 1
            // ------------
            // * (A) Find the first name(s) with the most letters
            // * (B) Find the first name(s) with the fewest letters
            ResultSet rst = stmt.executeQuery(
                    "SELECT DISTINCT First_Name, " +
                                    "Length(First_Name) AS Name_Length " +
                        "FROM " + UsersTable + " " +
                        "ORDER BY Name_Length DESC, First_Name ASC");

            rst.first();
            int mostLetters = rst.getInt(2);
            rst.last();
            int leastLetters = rst.getInt(2);

            FirstNameInfo info = new FirstNameInfo();

            rst.first();
            while (rst.next()) { // step through result rows/records one by one
                if (rst.getInt(2) == mostLetters) {
                    info.addLongName(rst.getString(1));
                }
                if (rst.getInt(2) == leastLetters) {
                    info.addShortName(rst.getString(1));
                }
            }

            // Step 2
            // ------------
            // * (C) Find the first name held by the most users
            // * (D) Find the number of users whose first name is that identified in (C)
            rst = stmt.executeQuery(
                    "SELECT DISTINCT First_Name, " +
                                    "COUNT(First_Name) AS Name_Count " +
                        "FROM " + UsersTable + " " + 
                        "ORDER BY Name_Count DESC, First_Name ASC");
            
            int most_users = 0;

            while(rst.next()) {
                if(rst.isFirst()) { // First entry has the most users with that name
                    most_users = rst.getInt(2);
                }
                if(rst.getInt(2) == most_users) { // Any name with as many users as the first (including itself) is also the most common
                    info.addCommonName(rst.getString(1));
                }
            }
            info.setCommonNameCount(most_users);

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close();

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }

    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT u.User_ID, u.First_Name, u.Last_Name " + 
                    "FROM " + UsersTable + " u " + 
                    "WHERE NOT EXISTS (" + 
                        "SELECT 1 " + 
                        "FROM " + FriendsTable + " f " + 
                        "WHERE u.User_ID = f.User1_ID OR u.User_ID = f.User2_ID" +
                    ") " +
                    "ORDER BY u.User_ID ASC");
        
            while(rst.next()) {
                UserInfo utemp = new UserInfo(rst.getInt(1), rst.getString(2), rst.getString(3));
                results.add(utemp);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT u.User_ID, " +
                        "u.First_Name, " +
                        "u.Last_Name " + 
                    "FROM " + UsersTable + " u " + 
                    "JOIN " + CurrentCitiesTable + " c " + 
                        "ON u.User_ID = c.User_ID " + 
                    "JOIN " + HometownCitiesTable + " h " + 
                        "ON u.User_ID = h.User_ID " + 
                    "WHERE c.Current_City_ID <> h.Hometwon_City_ID " + 
                    "ORDER BY u.User_ID");
            
            while(rst.next()) {
                UserInfo utemp = new UserInfo(rst.getInt(1), rst.getString(2), rst.getString(3));
                results.add(utemp);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */
            
            // Step 1
            // ------------
            // * (A) Find <num> most-tagged photos
            ResultSet rst = stmt.executeQuery(
                "SELECT p.photo_id, " +
                        "p.photo_link, " + 
                        "a.album_id, " +
                        "a.album_name, " +
                        "COUNT(DISTINCT t.tag_subject_id) AS Num_Tags " +
                    "FROM " + PhotosTable + " p " + 
                    "JOIN " + AlbumsTable + " a " + 
                        "ON p.album_id = a.album_id " + 
                    "LEFT JOIN " + TagsTable + " t " + 
                        "ON p.photo_id = t.tag_photo_id " +
                    "GROUP BY p.photo_id, p.photo_link, a.album_id, a.album_name " +
                    "ORDER BY Num_Tags DESC, p.photo_id ASC " + 
                    "FETCH FIRST " + String.valueOf(num) + "ROWS ONLY");
                
            while(rst.next()) {
                int photoID = rst.getInt(1);
                PhotoInfo p = new PhotoInfo(photoID, rst.getInt(3), rst.getString(2), rst.getString(4));
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);

                ResultSet rst2 = stmt.executeQuery(
                    "SELECT u.user_id, " + 
                            "u.first_name, " +
                            "u.last_name " + 
                        "FROM " + PhotosTable + " p " + 
                        "JOIN " + TagsTable + " t " +
                            "ON p.photo_id = t.tag_photo_id " + 
                        "JOIN " + UsersTable + " u " + 
                            "ON t.tag_subject_id = u.user_id " + 
                        "WHERE p.photo_id = " + String.valueOf(photoID) + " " + 
                        "ORDER BY u.user_id");
                while(rst2.next()) {
                    UserInfo utemp = new UserInfo(rst2.getInt(1), rst2.getString(2), rst2.getString(3));
                    tp.addTaggedUser(utemp);
                }
                results.add(tp);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */

            ResultSet rst = stmt.executeQuery(
                "SELECT u1.user_id, " + 
                        "u1.first_name, " +
                        "u1.last_name, " +
                        "u1.year_of_birth, " + 
                        "u2.user_id, " +
                        "u2.first_name, " +
                        "u2.last_name, " + 
                        "u2.year_of_birth " + 
                    "FROM " + UsersTable + " u1 " +
                    "JOIN " + UsersTable + " u2 " + 
                        "ON u1.gender = u2.gender " +
                    "WHERE u1.user_id < u2.user_id " + 
                    "AND EXISTS (" + 
                        "SELECT 1 " + 
                        "FROM " + TagsTable + " t1 " +
                        "JOIN " + TagsTable + " t2 " + 
                            "ON t1.tag_photo_id = t2.tag_photo_id " +
                        "WHERE t1.tag_subject_id = u2.user_id " +
                        "AND t2.tag_subject_id = u2.user_id" + 
                        ") " + 
                    "AND ABS(u1.year_of_birth - u2.year_of_birth) <= " + String.valueOf(yearDiff) + " " +
                    "AND NOT EXISTS (" +
                        "SELECT 1 " + 
                        "FROM " + FriendsTable + " f " +
                        "WHERE f.user1_id = u1.usr_id " +
                        "AND f.user2_id = u2.user_id" + 
                        ") " + 
                    "GROUP BY u1.user_id, u1.first_name, u1.last_name, u1.year_of_birth, " + 
                            "u2.user_id, u2.first_name, u2.last_name, u2.year_of_birth " +
                    "ORDER BY num_tags, u1.user_id, u2.user_id " + 
                    "FETCH FIRST " + String.valueOf(num) + " ROWS ONLY");
            while(rst.next()) {
                int uid1 = rst.getInt(1), uid2 = rst.getInt(5);
                UserInfo u1 = new UserInfo(uid1, rst.getString(2), rst.getString(3));
                UserInfo u2 = new UserInfo(uid2, rst.getString(6), rst.getString(7));
                MatchPair mp = new MatchPair (u1, rst.getInt(4), u2, rst.getInt(8));

                ResultSet rst = stmt.executeQuery(
                    "SELECT p.photo_id, " + 
                            "p.photo_link, " +
                            "a.album_id, " +
                            "a.album_name " + 
                        "FROM " + PhotosTable + " p " + 
                        "JOIN " + AlbumsTable + " a " + 
                            "ON p.album_id = a.album_id " + 
                        "WHERE EXISTS(" + 
                            "SELECT 1 " + 
                            "FROM " + TagsTable + " t " + 
                            "WHERE t.tag_subject_id = " + String.getValueOf(uid1) + " " +
                            "AND t.tag_photo_id = p.photo_id" + 
                            ") " + 
                        "AND EXISTS(" +
                            "SELECT 1 " + 
                            "FROM " + TagsTable + " t " + 
                            "WHERE t.tag_subject_id = " + String.getValueOf(uid2) + " " +
                            "AND t.tag_photo_id = p.photo_id" +
                            ") " + 
                        "ORDER BY p.photo_id");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            ResultSet rst = stmt.executeQuery(
                "SELECT  u1.user_id, \n" + //
                "        u1.first_name, \n" + //
                "        u1.last_name, \n" + //
                "        u2.user_id, \n" + //
                "        u2.first_name, \n" + //
                "        u2.last_name, \n" + //
                "        COUNT(*) AS Mutual_Count\n" + //
                "-- select ALL pairs of users\n" + //
                "FROM " + UsersTable + " u1\n" + //
                "JOIN " + UsersTable + " u2\n" + //
                "    ON u1.user_id < u2.user_id\n" + //
                "-- (i) find pairs of friends in common\n" + //
                "-- (i)(a) find friends of u1 that are not u2\n" + //
                "JOIN " + FriendsTable + " f1\n" + //
                "    ON (\n" + //
                "        (u1.user_id = f1.user1_id AND u2.user_id <> f1.user2_id) \n" + //
                "        OR \n" + //
                "        (u1.user_id = f1.user2_id AND u2.user_id <> f1.user1_id)\n" + //
                "    )\n" + //
                "-- (i)(b) select only friends of u1 that are friends of u2\n" + //
                "WHERE EXISTS (\n" + //
                "    SELECT 1\n" + //
                "    FROM " + FriendsTable + " f2\n" + //
                "    WHERE (\n" + //
                "        (\n" + //
                "            f2.user1_id = u2.user_id \n" + //
                "            AND \n" + //
                "            (\n" + //
                "                (f1.user1_id = f2.user2_id AND f1.user1_id <> u1.user_id)\n" + //
                "                OR\n" + //
                "                (f1.user2_id = f2.user2_id AND f1.user2_id <> u1.user_id)\n" + //
                "            )\n" + //
                "        )\n" + //
                "        OR\n" + //
                "        (\n" + //
                "            f2.user2_id = u2.user_id \n" + //
                "            AND \n" + //
                "            (\n" + //
                "                (f1.user1_id = f2.user1_id AND f1.user1_id <> u1.user_id)\n" + //
                "                OR\n" + //
                "                (f1.user2_id = f2.user1_id AND f1.user2_id <> u1.user_id)\n" + //
                "            )\n" + //
                "        )\n" + //
                "    )\n" + //
                ")\n" + //
                "-- ensures no duplicates or self-pairs\n" + //
                "AND u1.user_id < u2.user_id\n" + //
                "-- (ii) ensure users are not already friends\n" + //
                "AND NOT EXISTS (\n" + //
                "    SELECT 1\n" + //
                "    FROM " + FriendsTable + " f3\n" + //
                "    WHERE f3.user1_id = u1.user_id\n" + //
                "    AND f3.user2_id = u2.user_id\n" + //
                "    -- u1.user_id < u2.user_id and f3.user1_id < f3.user2_id -> \n" + //
                "    -- no need to check if f3.user2_id = u1.user_id and f3.user1_id = u2.user_id\n" + //
                ") \n" + //
                "GROUP BY u1.user_id, \n" + //
                "         u1.first_name, \n" + //
                "         u1.last_name, \n" + //
                "         u2.user_id, \n" + //
                "         u2.first_name, \n" + //
                "         u2.last_name\n" + //
                "ORDER BY Mutual_Count DESC, u1.user_id ASC, u2.user_id ASC\n" + //
                "FETCH FIRST " + num + " ROWS ONLY;"
            );

            while (rst.next()) {
                // get u1
                int u1_id = rst.getInt(1);
                String u1_first_name = rst.getString(2);
                String u1_last_name = rst.getString(3);

                UserInfo u1 = new UserInfo(u1_id, u1_first_name, u1_last_name);

                // get u2
                int u2_id = rst.getInt(4);
                String u2_first_name = rst.getString(5);
                String u2_last_name = rst.getString(6);

                UserInfo u2 = new UserInfo(u2_id, u2_first_name, u2_last_name);

                // make pair
                UsersPair up = new UsersPair(u1, u2);

                // get mutuals
                ResultSet rst_b = stmt.executeQuery(
                    "SELECT  u.user_id, \n" + //
                    "        u.first_name, \n" + //
                    "        u.last_name\n" + //
                    "FROM project2.Public_Users u -- FakebookOracleConstants.UsersTable u\n" + //
                    "WHERE EXISTS (\n" + //
                    "    SELECT 1\n" + //
                    "    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f\n" + //
                    "    WHERE (\n" + //
                    "        (f.user1_id = u.user_id AND f.user2_id = 42) -- FIXME: replace with a variable in java\n" + //
                    "        OR \n" + //
                    "        (f.user2_id = u.user_id AND f.user1_id = 42) -- FIXME: replace with a variable in java\n" + //
                    "    )\n" + //
                    ") \n" + //
                    "AND EXISTS (\n" + //
                    "    SELECT 1\n" + //
                    "    FROM project2.Public_Friends f -- FakebookOracleConstants.FriendsTable f\n" + //
                    "    WHERE (\n" + //
                    "        (f.user1_id = u.user_id AND f.user2_id = 318) -- FIXME: replace with a variable in java\n" + //
                    "        OR \n" + //
                    "        (f.user2_id = u.user_id AND f.user1_id = 318) -- FIXME: replace with a variable in java\n" + //
                    "    )\n" + //
                    ")\n" + //
                    "ORDER BY u.user_id;"
                );

                while (rst_b.next()) {
                    int u3_id = rst_b.getInt(1);
                    String u3_first_name = rst_b.getString(2);
                    String u3_last_name = rst_b.getString(3);

                    UserInfo u3 = new UserInfo(u3_id, u3_first_name, u3_last_name);

                    up.addSharedFriend(u3);
                }

                // * Close resources being used
                rst.close();
                rst_b.close();
                stmt.close();

                results.add(up);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            ResultSet rst = stmt.executeQuery(
                "SELECT c.state_name, \n" + //
                "        COUNT(e.event_id) AS Num_Events\n" + //
                "FROM " + CitiesTable + " c\n" + //
                "JOIN " + EventsTable + " e\n" + //
                "ON c.city_id = e.event_city_id\n" + //
                "GROUP BY c.state_name\n" + //
                "ORDER BY Num_Events DESC, c.state_name ASC;"
            );
            
            rst.first();
            int most_events = rst.getInt(3);

            EventStateInfo info = new EventStateInfo(most_events);

            while (rst.next()) {
                if (rst.getInt(3) == most_events) {
                    info.addState(rst.getString(1));
                }
            }

            // * Close resources being used
            rst.close();
            stmt.close();

            return info;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            ResultSet rst = stmt.executeQuery(
                "SELECT  u.user_id, \n" + //
                "        u.first_name, \n" + //
                "        u.last_name, \n" + //
                "        u.year_of_birth, \n" + //
                "        u.month_of_birth, \n" + //
                "        u.day_of_birth\n" + //
                "FROM " + UsersTable + " u\n" + //
                "JOIN " + FriendsTable + " f\n" + //
                "    ON (\n" + //
                "        (f.user1_id = u.user_id AND f.user2_id = " + userID + ")\n" + //
                "        OR \n" + //
                "        (f.user2_id = u.user_id AND f.user1_id = " + userID + ")\n" + //
                "    )\n" + //
                "ORDER BY u.year_of_birth ASC, u.month_of_birth ASC, u.day_of_birth ASC, u.user_id ASC;"
            );

            rst.first();
            int oldest = rst.getInt(1);
            String oldest_first_name = rst.getString(2);
            String oldest_last_name = rst.getString(3);
            int oldest_year = rst.getInt(4);
            int oldest_month = rst.getInt(5);
            int oldest_day = rst.getInt(6);
            while (rst.next()) {
                if (rst.getInt(4) == oldest_year && rst.getInt(5) == oldest_month && rst.getInt(6) == oldest_day) {
                    oldest = rst.getInt(1);
                    oldest_first_name = rst.getString(2);
                    oldest_last_name = rst.getString(3);
                } else break;
            }
            rst.last();
            int youngest = rst.getInt(1);
            String youngest_first_name = rst.getString(2);
            String youngest_last_name = rst.getString(3);

            UserInfo old = new UserInfo(oldest, oldest_first_name, oldest_last_name);
            UserInfo young = new UserInfo(youngest, youngest_first_name, youngest_last_name);

            // * Close resources being used
            rst.close();
            stmt.close();

            return new AgeInfo(old, young);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }

    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            ResultSet rst = stmt.executeQuery(
                "SELECT  u1.user_id, \n" + //
                "        u1.first_name, \n" + //
                "        u1.last_name, \n" + //
                "        u2.user_id, \n" + //
                "        u2.first_name, \n" + //
                "        u2.last_name\n" + //
                "FROM " + UsersTable + "\n" + //
                "-- (i) same last name\n" + //
                "JOIN " + UsersTable + " u2\n" + //
                "    ON u1.last_name = u2.last_name\n" + //
                "-- (ii) same hometown\n" + //
                "WHERE EXISTS (\n" + //
                "    SELECT 1\n" + //
                "    FROM " + HometownCitiesTable + " h1\n" + //
                "    JOIN " + HometownCitiesTable + " h2\n" + //
                "        ON h1.Hometown_City_ID = h2.Hometown_City_ID\n" + //
                "    WHERE h1.user_id = u1.user_id\n" + //
                "    AND h2.user_id = u2.user_id\n" + //
                ")\n" + //
                "-- (iii) are friends\n" + //
                "AND EXISTS (\n" + //
                "    SELECT 1\n" + //
                "    FROM " + FriendsTable + " f\n" + //
                "    WHERE f.user1_id = u1.user_id\n" + //
                "    AND f.user2_id = u2.user_id\n" + //
                ")\n" + //
                "-- (iv) less than 10 birth years apart\n" + //
                "AND ABS(u1.year_of_birth - u2.year_of_birth) < 10\n" + //
                "-- ensures no duplicates or self-pairs\n" + //
                "AND u1.user_id < u2.user_id\n" + //
                "ORDER BY u1.user_id, u2.user_id;"
            );

            while (rst.next()) { // step through result rows/records one by one
                // get user1 info
                int user_id1 = rst.getInt(1);
                String first_name1 = rst.getString(2);
                String last_name1 = rst.getString(3);

                // get user2 info
                int user_id2 = rst.getInt(4);
                String first_name2 = rst.getString(5);
                String last_name2 = rst.getString(6);

                // create users
                UserInfo user1 = new UserInfo(user_id1, first_name1, last_name1);
                UserInfo user2 = new UserInfo(user_id2, first_name2, last_name2);

                // create sibling pair
                SiblingInfo si = new SiblingInfo(user1, user2);

                // add to results
                results.add(si);
            }

            // * Close resources being used
            rst.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
