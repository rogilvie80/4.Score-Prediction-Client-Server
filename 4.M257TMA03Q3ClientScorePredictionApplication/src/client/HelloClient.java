package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


/**
 * Title:        HelloClient class
 * Description:  Client that connects to a server, receives and displays a
 *               message and then terminates
 * 
 * This class does not have any output streams set up yet, because
 * at the moment it only reads information from the server.
 * This class is based on Activity 9.6

 * @author M257 Course Team
 */
public class HelloClient extends JFrame implements ActionListener
{
    //Streams used for communicating with server
    private InputStream is;
    private BufferedReader fromServer;
    private OutputStream os;
    private PrintWriter toServer;
    private Socket socket;    // Socket to server
    private static final int SERVER_PORT_NUMBER = 3000;
    private JTextField player, prediction;
    private JButton registerAndSubmitButton, displayButton, quitButton, helpButton;
    private JTextArea displayPlayersArea;

    private final String CLIENT_QUITTING = "Exit";
    private final String DISPLAY_MESSAGE = "Display";
    private final String REGISTER_MESSAGE = "Register";
    private final String HELP_MESSAGE = "Help";

    public HelloClient(String event)
    {
        super(event);
    }

    //This is the client's main method - it performs a single
    //interaction with the server using the processHello method
    public void run()
    {
        //set up connection to the server
        try
        {
            connectToServer();
            setUpGUI();
            processHello();
        }
        catch (IOException e)
        {
            System.out.println("Exception in client run " + e);
        }
    }

    private void setUpGUI()
    {
        final int CLIENT_WINDOW_WIDTH = 400;
        final int CLIENT_WINDOW_HEIGHT = 400;
        final int TEXTFIELD_WIDTH = 30;

        player = new JTextField(TEXTFIELD_WIDTH);
        prediction = new JTextField("eg 8-6 or 7-7", TEXTFIELD_WIDTH);
        registerAndSubmitButton = new JButton("Register player and submit "
                + "score prediction");
        displayButton = new JButton("Display");
        quitButton = new JButton("Quit");
        displayPlayersArea = new JTextArea(10, 30);
        helpButton = new JButton("Help");

        Container content = getContentPane();
        content.setLayout(new FlowLayout());

        content.add(new Label("Player"));
        content.add(player);
        content.add(new Label("Prediction"));
        content.add(prediction);
        content.add(registerAndSubmitButton);
        content.add(displayButton);
        content.add(quitButton);
        content.add(displayPlayersArea);
        content.add(helpButton);

        registerAndSubmitButton.addActionListener(this);
        displayButton.addActionListener(this);
        quitButton.addActionListener(this);
        helpButton.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(CLIENT_WINDOW_WIDTH, CLIENT_WINDOW_HEIGHT);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent a)
    {
        Object buttonClicked = a.getSource();
        try
        {
            if(buttonClicked.equals(registerAndSubmitButton))
            {
                createPlayer();
            }
            if(buttonClicked.equals(displayButton))
            {
                displayPlayers();
            }
            if(buttonClicked.equals(quitButton))
            {
                processQuit();
            }
            if(buttonClicked.equals(helpButton))
            {
                processHelp();
            }
        }
        catch (IOException e)
        {
            System.out.println("Problem with the server " + e);
        }
    }


    //this method creates a socket on the local host to
    //the specified port number, for communications with the server
    private void connectToServer()
    {
        try
        {
            //this is a portable way of getting the local host address
            final InetAddress SERVER_ADDRESS = InetAddress.getLocalHost();
            System.out.println("Attempting to contact " + SERVER_ADDRESS);

            socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT_NUMBER);
            openStreams();
        }
        catch (IOException e)
        {
            String ls = System.getProperty("line.separator");
            System.out.println(ls + "Trouble contacting the server: " + e);
            System.out.println("Perhaps you need to start the server?");
            System.out.println("Make sure they're talking on the same port?" + ls);
        }
    }

    // open streams for communicating with the server
    private void openStreams() throws IOException
    {
        final boolean AUTO_FLUSH = true;
        is = socket.getInputStream();
        fromServer = new BufferedReader(new InputStreamReader(is));
        os = socket.getOutputStream();
        toServer = new PrintWriter(os, AUTO_FLUSH);
    }

    // close streams to server
    private void closeStreams() throws IOException
    {
        fromServer.close();
        is.close();
    }

    //An example method that completes a single interaction with the server
    //In this case, the client doesn't say anything to the server
    private void processHello() throws IOException
    {
        String messageFromServer = fromServer.readLine();
        displayPlayersArea.setText("Server said: " + messageFromServer); 
    }

    private void createPlayer() throws IOException
    {
        toServer.println(REGISTER_MESSAGE);
        String messageFromServer = fromServer.readLine();
        displayPlayersArea.setText(messageFromServer);
        String playerName;
        playerName = player.getText();
        String playerScore;
        playerScore = prediction.getText();
        System.out.println("Sending player to server");
        toServer.println(playerName);
        messageFromServer = fromServer.readLine();
        displayPlayersArea.setText(displayPlayersArea.getText() + "\n"
                + messageFromServer);
        System.out.println("Sending player prediction to server");
        toServer.println(playerScore);
        messageFromServer = fromServer.readLine();
        displayPlayersArea.setText(displayPlayersArea.getText() + "\n"
                + messageFromServer);
    }

    private void displayPlayers() throws IOException
    {
        toServer.println(DISPLAY_MESSAGE);
        displayPlayersArea.setText("");
        String messageFromServer = fromServer.readLine();
        while(!(messageFromServer.equals("Done")))
        {
            displayPlayersArea.setText(displayPlayersArea.getText()
                    + messageFromServer + "\n");
            messageFromServer = fromServer.readLine();
        }
    }

    private void processQuit() throws IOException
    {
        toServer.println(CLIENT_QUITTING);
        closeStreams();
        socket.close();
        System.exit(0);
    }

    private void processHelp() throws IOException
    {
        toServer.println(HELP_MESSAGE);
        displayPlayersArea.setText("");
        String messageFromServer = fromServer.readLine();
        displayPlayersArea.setText(messageFromServer);
    }
} // end class

