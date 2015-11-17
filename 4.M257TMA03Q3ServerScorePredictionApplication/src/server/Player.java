/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

/**
 *
 * @author ross
 */
public class Player {
    String playerName;
    String playerScore;

    public Player(String name)
    {
        playerName = name;
    }

    public String getName()
    {
        return this.playerName;
    }

    public void setPlayerScore(String score)
    {
        playerScore = score;
    }

    public String getScore()
    {
        return this.playerScore;
    }
}
