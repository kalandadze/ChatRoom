import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatRoom  {
    static ArrayList<User> users=new ArrayList<>();

    public int getUsersNum() {
        return users.size();
    }

    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
        System.out.print("port: ");
        int port=s.nextInt();

        ServerSocket server;
        try {
            server=new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(()->{
            while (true){
                Socket socket;
                try {
                    socket = server.accept();
                    System.out.println("User " + socket.getInetAddress().getHostName() + " established connection");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                User user=new User(socket);
                users.add(user);
                user.start();
            }
        }).start();
    }

    public void receive(String text,Socket sender) throws IOException {
        for (User user:users){
            if (user.getSocket()!=sender){
                user.send(text);
            }
        }
    }

}