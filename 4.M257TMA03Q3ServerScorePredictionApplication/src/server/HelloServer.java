package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Title:        HelloServer class
 * Description:  Server that sends a single message to a client
 * and then terminates.
 *
 * This class does not have any input streams set up yet, because at
 * the moment it only sends a message to the client.
 * This class is based on Activity 9.6
 *
 * @author M257 Course Team
 */
public class HelloServer
{
    private static final int PORT_NUMBER = 3000;
    
    private ArrayList playersScores;
    private Thread connection;

    // constructor
    public HelloServer()
    {
        System.out.println("...Server starting up");
        playersScores = new ArrayList<Player>();
    } // end constructor

    public void run()
    {
        try
        {
            ServerSocket ss = new ServerSocket(PORT_NUMBER);
            while(true)
            {
                Socket socket = ss.accept();
                connection = new Thread(new ConnectionThread(socket,
                        playersScores));
                connection.start();
            }
        }
        catch (IOException e)
        {
            System.out.println("Trouble with a connection " + e);
        }
    }
} // end class

