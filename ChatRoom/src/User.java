import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class User extends Thread{
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public User(Socket socket) {
        this.socket = socket;
    }

    public void send(String text) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        try {
            output.writeUTF(text);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            Scanner s=new Scanner(System.in);
            while (true){
                while (!isInterrupted()) {
                    try {
                        String inp=input.readUTF();
                        if (inp.split(":")[1].equalsIgnoreCase(" \\help")){
                            Thread help=new Thread(){
                                @Override
                                public void run(){
                                    boolean bool=true;
                                    while (bool){
                                        try {
                                            send("""

                                                    =====================================================
                                                    type "1" to message someone privately
                                                    type "2" to change nickname
                                                    type "3" to continue messaging
                                                    type "4" to exit programme
                                                    Number of active users:""" + new ChatRoom().getUsersNum() + """
                                                    \n=====================================================
                                                                                                    
                                                    """);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        String a;
                                        try {
                                            a=input.readUTF().split(":")[1];
                                            System.out.println(a);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        switch (a){
                                            case " 3":
                                                bool=false;break;
                                        }
                                    }
                                }
                            };
                            help.start();
                            help.join();
                        }else {
                            new ChatRoom().receive(inp, socket);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
