package server;


/**
 * Title:        TestHelloServer class
 * Description:  Tests the HelloServer class

 * @author M257 Course Team
 */

public class TestHelloServer
{
   // Create a server and have it greet the client
   public static void main(String[] args)
   {
      HelloServer server1 = new HelloServer();
      server1.run();
   }
}
