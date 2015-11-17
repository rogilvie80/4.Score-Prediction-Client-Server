package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author ross
 */

public class ConnectionThread implements Runnable
{
   private Socket socket;
   private List<Player> playersScores;

   //Streams for connections
   private InputStream is;
   private OutputStream os;
   private PrintWriter toClient;
   private BufferedReader fromClient;

   // Protocol definitions
   private static final String ALL_PLAYERS_DISPLAYED = "Done";
   private static final String CLIENT_QUITTING = "Exit";
   private static final String REGISTER_MESSAGE = "Register";
   private static final String DISPLAY_MESSAGE = "Display";
   private static final String HELP_MESSAGE = "Help";


   public ConnectionThread(Socket s, ArrayList al)
   {
      socket = s;
      playersScores = al;
   }

   public void run()
   {
      try
      {
            openStreams();
            processHello();
            String clientRequest = fromClient.readLine();
            while(!(clientRequest.equals(CLIENT_QUITTING)))
            {
                if(clientRequest.equals(REGISTER_MESSAGE))
                {
                    createPlayer();
                }
                if(clientRequest.equals(DISPLAY_MESSAGE))
                {
                    displayPlayers();
                }
                if(clientRequest.equals(HELP_MESSAGE))
                {
                    processHelp();
                }
                clientRequest = fromClient.readLine();
            }
            closeStreams();
            socket.close();
      }
      catch (Exception e)
      {
         System.out.println("Trouble with a connection " + e);
      }
   }

    private void processHello() throws IOException
    {
        System.out.println("Server is executing its hello method");
        toClient.println("Hello Client, this is the server, "
                + "awaiting player registration");
        System.out.println("done talking to client in hello method");
    }

    private void createPlayer() throws IOException
    {
        toClient.println("Server is creating new Player");
        String playerName;
        playerName = fromClient.readLine();
        playersScores.add(new Player(playerName));
        System.out.println("player registered");
        toClient.println("Submitting score prediction");
        String playerScore;
        playerScore = fromClient.readLine();
        for(Player eachPlayer : playersScores)
        {
            if(eachPlayer.getName().equals(playerName))
            {
                eachPlayer.setPlayerScore(playerScore);
            }
        }
        toClient.println("Player registered, press display to see all "
                + "players predictions or quit");
    }

    private void displayPlayers() throws IOException
    {
        String display = "";
        for(Player eachPlayer : playersScores)
        {
            display = eachPlayer.getName() + ": " + eachPlayer.getScore();
            toClient.println(display);
        }
        toClient.println(ALL_PLAYERS_DISPLAYED);
    }

    private void processHelp()
    {
        String helpMessage;
        helpMessage = "Enter your name and your correct score prediction. "
                + "The darts match is best of 14 legs, in other words"
                + "first to 8 or a 7-7 draw. "
                + "Press Display to see all players who have submitted their"
                + "score so far or press Quit to exit the client.";
        toClient.println(helpMessage);
    }

    // set up streams for communicating with the client
    private void openStreams() throws IOException
    {
        final boolean AUTO_FLUSH = true;
        os = socket.getOutputStream();
        toClient = new PrintWriter(os, AUTO_FLUSH);
        is = socket.getInputStream();
        fromClient = new BufferedReader(new InputStreamReader(is));
        System.out.println("...Streams set up");
    }

    // close output streams to client
    private void closeStreams() throws IOException
    {
        toClient.close();
        os.close();
        System.out.println("...Streams closed down");
    }

}