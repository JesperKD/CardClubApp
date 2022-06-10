package com.example.cardclub.serverHandling;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class handling all data going to the server
 */
public class ServerOutput extends Thread{
    private DataOutputStream output = null;
    private final String msg;

    public ServerOutput(Socket socket, String msg)
    {
        this.msg = msg;
        try {
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        try {
            output.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
