package com.example.cardclub.serverHandling;

import com.example.cardclub.MainActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class for handling all server traffic from the server
 */
public class ServerInput  extends Thread{

    private final MainActivity activity;
    private DataInputStream input = null;

    public ServerInput(MainActivity mainActivity, Socket socket)
    {
        this.activity = mainActivity;
        try {
            input = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        String line = "";
        while(!(line.equals("Done")))
        {
            try {
                line = input.readUTF();
                activity.setMessageView(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
