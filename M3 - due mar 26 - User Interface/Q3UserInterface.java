import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

// Assuming the postgresql jar is in the same folder as this:
    // Compile with: javac -classpath postgresql.jar Q3UserInterface.class
    // Then run with: java -classpath postgresql.jar:. Q3UserInterface
public class Q3UserInterface
{
    private static Connection con;

    private static final Scanner KEYBOARD = new Scanner(System.in);

    private static final String MENU_TEXT = "\nPlease selected one of the following options by number:\n"
        + "1) Get basic information on an album\n"
        + "2) 10% off all poorly rated products\n"
        + "3) Update the price of an album\n"
        + "4) Find songs by country and extension\n"
        + "5) Remove artist and all associated products from the database\n"
        + "6) Quit\n";

    private static final String USERNAME = "cs421g03";
    private static final String PASSWORD = "muvfoytt";

    public static void main ( String [ ] args ) throws SQLException
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch(ClassNotFoundException e){e.printStackTrace();}
    
        String url = "jdbc:postgresql://db2/CS421";
        con = DriverManager.getConnection (url, USERNAME, PASSWORD) ;

        // Interface starts here
        System.out.println("Welcome back, " + USERNAME);

        int input = -1;
        while(input != 6)
        {
            System.out.println(MENU_TEXT);

            // get selection from user
            try
            {
                input = KEYBOARD.nextInt();
            } 
            catch(InputMismatchException e)
            { 
                input = 2112; 
            }
            finally
            {
                KEYBOARD.nextLine();
            }

            // Act based on user selection
            switch(input)
            {
            case 1:
                getBasicAlbumInfo();
                break;
            case 2:
                beginSale();
                break;
            case 3:
                updateAlbumPrice();
                break;
            case 4:
                findSongsByCountryAndExtension();
                break;
            case 5:
                removeArtistAndDiscography();
                break;
            case 6:
                System.out.println("Goodbye.");
                break;
            default:
                System.out.println("That is not a valid input, please try again.");
                break;
            }
            System.out.println(" ");
        }
        con.close();
    }

    // Gets input from user and queries the SongPlayerInfo view
    public static void getBasicAlbumInfo()
    {
        System.out.println("Please enter the album name:");
        String albumName = KEYBOARD.nextLine();

        try
        {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT * FROM SongPlayerInfo WHERE Album = ? ORDER BY TrackNo ASC;");
            stmt.setString(1, albumName);
            ResultSet results = stmt.executeQuery();

            while(results.next())
            {
                String song = results.getString("Song");
                String artist = results.getString("Artist");
                String album = results.getString("Genre");
                int track = results.getInt("TrackNo");

                System.out.printf("Song: %s\nArtist: %s\nGenre: %s\nTrackNo: %d\n%n", song, artist, album, track);
            }
            stmt.close();
        }
        catch(SQLException e)
        {
            // shouldn't happen
            e.printStackTrace();
        }
    }

    // takes 10% off all poorly rated products
    public static void beginSale()
    {
        String query = "UPDATE product SET price = price - price/10 WHERE product.pid = ANY ( SELECT product.pid FROM product, rating WHERE product.pid = rating.pid AND rating.rating_amt < 2);";

        try
        {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query);
            int results = stmt.executeUpdate();
            System.out.println(results + " products are now 10% off.");
            stmt.close();
        }
        catch(SQLException e)
        {
            // shouldn't happen
            e.printStackTrace();
        }
    }

    // updates the price of an album
    public static void updateAlbumPrice()
    {
        // get input
        System.out.println("Please enter the album name:");
        String albumName = KEYBOARD.nextLine();

        System.out.println("Please enter the new price of the album:");
        double newPrice = -1.0;
        while( newPrice < 0)
        {
            try
            {
                newPrice = KEYBOARD.nextDouble();
                if(newPrice < 0)
                    System.out.println("Please enter a non-negative price.");
            }
            catch( InputMismatchException e)
            {
                System.out.println("Please enter a valid price.");
            }
            finally
            {
                KEYBOARD.nextLine();
            }
        }

        DecimalFormat df = new DecimalFormat("#.00");

        // execute the query
        String query = "UPDATE product SET price = \'" + df.format(newPrice) + "\'"
                        +" WHERE pid IN ( "
                        +" SELECT product.pid FROM product, album WHERE product.pid = album.pid AND album.name = ?"
                        +");";

        try
        {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query);
            stmt.setString(1, albumName);
            int results = stmt.executeUpdate();
            System.out.println("Successfully changed the price of " + results + " albums with the name " + albumName + ".");
            stmt.close();
        }
        catch(SQLException e)
        {
            // shouldn't happen
            e.printStackTrace();
        }
    }

    // TODO finish and test
    public static void findSongsByCountryAndExtension()
    {
        System.out.println("Please enter the extension type:");
        String ext = KEYBOARD.nextLine();

        System.out.println("Please enter the country:");
        String country = KEYBOARD.nextLine();

        String formatQuery = "SELECT song.title FROM song "
            + "INNER JOIN song_artist ON song.pid = song_artist.sid "
            + "INNER JOIN artist ON artist.artid = song_artist.artid "
            + "INNER JOIN country ON country.coid = artist.location_of_origin_id "
            + "INNER JOIN format ON format.fid = song.format_id "
            + "WHERE format.extension = ?;";

        String countryQuery = "SELECT song.title FROM song "
            + "INNER JOIN song_artist ON song.pid = song_artist.sid "
            + "INNER JOIN artist ON artist.artid = song_artist.artid "
            + "INNER JOIN country ON country.coid = artist.location_of_origin_id AND country.name = ?;";

        int numResults = 0;
        ArrayList<String> resultNames = new ArrayList<String>();

        try
        {
            ArrayList<String> songTitles = new ArrayList<String>();

            // query1
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(formatQuery);
            stmt.setString(1, ext);
            ResultSet results = stmt.executeQuery();

            while(results.next())
            {
                String songTitle = results.getString("title");
                songTitles.add(songTitle);
            }
            stmt.close();

            // query2
            stmt = con.prepareStatement(countryQuery);
            stmt.setString(1, country);
            results = stmt.executeQuery();

            while(results.next())
            {
                String songTitle = results.getString("title");

                if(songTitles.contains(songTitle))
                {
                    numResults++;
                    resultNames.add(songTitle);
                }
            }

        } catch(SQLException e){e.printStackTrace();}

        System.out.println("\nFound " + numResults + "results:");
        for(String s : resultNames)
        {
            System.out.println(s);
        }
        System.out.println(" ");
    }

    // Removes all artists by a given name and their associated products (albums/songs)
    public static void removeArtistAndDiscography()
    {
        System.out.println("Please enter the name of the artist to be removed from the database along with all of their associated products:");
        String artist = KEYBOARD.nextLine();

        ArrayList<Integer> artistIDs = new ArrayList<Integer>();
        int removedArtists = 0;
        int removedProducts = 0;

        try
        {
            // get the matching artists
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT artid FROM artist WHERE name = ?");
            stmt.setString(1, artist);
            ResultSet results = stmt.executeQuery();
           
            while(results.next())
            {
                int artID = results.getInt("artid");
                artistIDs.add(new Integer(artID));
            }
            stmt.close();

            // delete all associated products
            String query = "DELETE FROM album WHERE album.pid IN (SELECT album_artist.albid FROM album_artist WHERE album_artist.artid = ?);";
            for(Integer artistID : artistIDs)
            {
                stmt = con.prepareStatement(query);
                stmt.setInt(1, artistID);
                removedProducts += stmt.executeUpdate();
                stmt.close();
            }
            // delete the artists
            for(Integer artistID : artistIDs)
            {
                stmt = con.prepareStatement("DELETE FROM artist WHERE artid = ?");
                stmt.setInt(1, artistID);
                removedArtists += stmt.executeUpdate();
                stmt.close();
            }
            System.out.println("Removed " + removedArtists + " artists by the name of " + artist + " and " + removedProducts + " associated albums");
        }
        catch(SQLException e)
        {
            // shouldn't happen
            e.printStackTrace();
        }
    }
}