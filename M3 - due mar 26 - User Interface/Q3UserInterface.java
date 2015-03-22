import java.sql.*;
import java.util.Scanner;

// Assuming the postgresql jar is in the same folder as this:
	// Compile with: javac -classpath postgresql.jar Q3UserInterface.class
	// Then run with: java -classpath postgresql.jar Q3UserInterface
public class Q3UserInterface
{
	private static Connection con;

	private static final Scanner KEYBOARD = new Scanner(System.in);

	private static final String MENU_TEXT = "\nPlease selected one of the following options by number:\n"
		+ "1) Get basic information on an album\n"
		+ "2) Do a fancy thing\n"
		+ "3) Modify a thing\n"
		+ "4) Add a thing\n"
		+ "5) Look up a cool thing\n"
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
				System.out.println("enter an integer");
				input = KEYBOARD.nextInt();
				KEYBOARD.nextLine();
			} 
			catch(java.util.InputMismatchException e)
			{ 
				input = 2112; 
			}

			// Act based on user selection
			switch(input)
			{
			// TODO
			case 1:
				getBasicAlbumInfo();
				break;
			case 2:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				System.out.println("Goodbye.");
				break;
			default:
				System.out.println("That is not a valid input, please try again");
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
    		e.printStackTrace();
    	}
    }
}