import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

// Assuming the postgresql jar is in the same folder as this:
    // Compile with: javac -classpath postgresql.jar Q3UserInterface.java
    // Then run with: java -classpath postgresql.jar:. Q3UserInterface
    // for windows, do java -classpath postgresql.jar;. Q3UserInterface
public class Q3UserInterface
{
    private static Connection con;

    private static final Scanner KEYBOARD = new Scanner(System.in);

    private static final String MENU_TEXT = "\nPlease selected one of the following options by number:\n"
        + "1) Get basic information on an album\n"
        + "2) 10% off all poorly rated products\n"
        + "3) Update the price of an album\n"
        + "4) Find the number of songs by country and extension\n"
        + "5) Remove artist and all associated products from the database\n"
	+ "6) Add indices to the database\n"
        + "7) Quit\n";

    private static final String USERNAME = "cs421g03";
    private static final String PASSWORD = "muvfoytt";

    public static void main ( String [ ] args ) throws SQLException
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch(ClassNotFoundException e){e.printStackTrace();}
    
        String url = "jdbc:postgresql://db2.cs.mcgill.ca/CS421";
        con = DriverManager.getConnection (url, USERNAME, PASSWORD) ;

        // Interface starts here
        System.out.println("Welcome back, " + USERNAME);

        int input = -1;
        while(input != 7)
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
                numberOfSongsByExtensionAndCountry();
                break;
            case 5:
                removeArtistAndDiscography();
                break;
	    case 6:
		addIndices();
		break;
            case 7:
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
    public static void numberOfSongsByExtensionAndCountry()
    {
        System.out.println("Please enter the extension type:");
        String ext = KEYBOARD.nextLine();

        System.out.println("Please enter the country:");
        String country = KEYBOARD.nextLine();

        String query1 = "SELECT create_songs_extension_by_country(?);";
        String query2 = "SELECT song_count FROM extension_songs_by_country WHERE cname = ?";


        int numResults = 0;

        try
        {
            // query1
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query1);
            stmt.setString(1, ext);
            ResultSet results = stmt.executeQuery();
            stmt.close();

            // query2
            stmt = con.prepareStatement(query2);
            stmt.setString(1, country);
            results = stmt.executeQuery();

            while(results.next())
            {
                numResults += results.getInt("song_count");
            }
            stmt.close();

        } catch(SQLException e){e.printStackTrace();}

        System.out.println(numResults + " songs found of format" + ext + " from " + country + ".\n");
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

    // Runs queries before and after adding indices to see speed up
    public static void addIndices()
    {
    	System.out.println("Testing the effect of two indices on two queries: ");
    	// this is query 1 from Q5 of Milestone 2
        String genreNameQuery = "SELECT genre.genid, genre.name, COUNT(Genre.genid) FROM genre JOIN song_genre ON genre.genid = song_genre.genid JOIN song ON song_genre.sid = song.pid JOIN product ON song.pid = product.pid JOIN purchase_product ON purchase_product.pid = product.pid GROUP BY genre.genid, genre.name;";
        // this is query 3 from Q5 of Milestone 2
        String artistNameQuery = "SELECT artist.name, AVG(rating_amt) FROM artist JOIN song_artist ON artist.artid = song_artist.artid JOIN song ON song_artist.sid = song.pid JOIN rating ON rating.pid = song.pid GROUP BY artist.name;";
        
        
        String createGenreNameIndex = "CREATE INDEX genrename ON genre(name);";
        String createArtistNameIndex = "CREATE INDEX artistname ON artist(name);";
        
        String deleteGenreNameIndex = "DROP INDEX genrename;";
        String deleteArtistNameIndex = "DROP INDEX artistname;";
        
        long beforeExecution;
        long afterExecution;
       
        // first try deleting indices if they already exist
        try
        {         
	     PreparedStatement stmt = null;
            stmt = con.prepareStatement(deleteGenreNameIndex);
            int results = stmt.executeUpdate(); 
            stmt.close();
         
            stmt = con.prepareStatement(deleteArtistNameIndex);
            results = stmt.executeUpdate(); 
            stmt.close();
        }
        catch(SQLException e)
        {

        }        
        
        try
        {
            System.out.println("Query 1: ");
	     System.out.println(genreNameQuery);
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(genreNameQuery);
            beforeExecution = System.currentTimeMillis();
            stmt.executeQuery(); 
            afterExecution = System.currentTimeMillis();
            System.out.println("Query 1 took " + (afterExecution - beforeExecution) + " miliseconds to complete before adding indices.");
            
            stmt = con.prepareStatement(createGenreNameIndex);
            stmt.executeUpdate(); 
            System.out.println("Index on genre.name added.");
            stmt.close();
            
            stmt = con.prepareStatement(genreNameQuery);
            beforeExecution = System.currentTimeMillis();
            stmt.executeQuery(); 
            afterExecution = System.currentTimeMillis();
            System.out.println("Query 1 took " + (afterExecution - beforeExecution) + " miliseconds to complete after adding indices.");
            
            System.out.println("Query 2: ");
	     System.out.println(artistNameQuery);            
            stmt = con.prepareStatement(artistNameQuery);
            beforeExecution = System.currentTimeMillis();
            stmt.executeQuery(); 
            afterExecution = System.currentTimeMillis();
            System.out.println("Query 2 took " + (afterExecution - beforeExecution) + " miliseconds to complete before adding indices.");
            
            stmt = con.prepareStatement(createArtistNameIndex);
            stmt.executeUpdate(); 
            System.out.println("Index on artist.name added.");
            stmt.close();
            
            stmt = con.prepareStatement(artistNameQuery);
            beforeExecution = System.currentTimeMillis();
            stmt.executeQuery(); 
            afterExecution = System.currentTimeMillis();
            System.out.println("Query 2 took " + (afterExecution - beforeExecution) + " miliseconds to complete after adding indices.");
        }
        catch(SQLException e)
        {
            // shouldn't happen
            e.printStackTrace();
        }
    }
}
