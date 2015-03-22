import java.sql.*;
import java.util.Scanner;

// Compile with: javac -classpath postgresql.jar Q3UserInterface.class
	// assuming the postgresql jar is in the same folder as this
public class Q3UserInterface
{
	private static final String MENU_TEXT = "\nPlease selected one of the following options by number:\n"
		+ "1) Look up a thing\n"
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
		Connection con = DriverManager.getConnection (url, USERNAME, PASSWORD) ;
		Statement statement = con.createStatement ( ) ;


		// Interface starts here
		System.out.println("Welcome back, " + USERNAME);

		int input = -1;
		while(input != 6)
		{
			System.out.println(MENU_TEXT);

			try
			{
				input = parseInput();
			} catch(java.util.InputMismatchException e){ input = 2112; }


			switch(input)
			{
			// TODO
			case 1:
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
		con.close ( ) ;
    }

    public static int parseInput()
    {
    	Scanner keyboard = new Scanner(System.in);
		System.out.println("enter an integer");
		return keyboard.nextInt();
    }
}