package me.daniel.chargen;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private Chargen gen;
    
    public ClientThread(Socket socket) {
        this.socket = socket;
        this.gen = new Chargen();
        
        new Thread(this).start();
    }
    
    public void run() {
        DataOutputStream dos = getOutput();
        while(true) {
            if(!write(gen.next(), dos)) break;
            sleep(10);
        }
        
        close();
    }
    
    //All of these helper methods are done to avoid having a bunch 
    //of try/catches in the run method
    
    private DataOutputStream getOutput() {
        try {
            return new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            return null;
        }
    }
    
    private boolean write(String s, DataOutputStream dos) {
        try {
            dos.write(s.getBytes());
            dos.flush();
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    //Delay the thread for this many ms.
    //Prevents the client from using a load of their CPU
    //for this stupid program lol
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException ignored) {}
    }
    
    //Cleans up this client so it's ready 
    //to be deleted by the ChargenServer janitor
    private void close() {
        try {
            socket.close();
        } catch(IOException ignored) {}

        socket = null;
        gen = null;
    }
    
    public boolean done() {
        return gen == null;
    }
    
}
