import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatUser {
    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
        System.out.print("Address: ");
        String address=s.next();
        System.out.print("port: ");
        int port=s.nextInt();
        System.out.print("UserName: ");
        String username=s.next();

        Socket socket;
        try {
            socket=new Socket(address,port);
            System.out.println("Connected to " + address + ":" + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\n==================================================================");
        System.out.println("type \"\\help\" for more commands");
        System.out.println("==================================================================\n");

        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            Thread reader = new Thread() {
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            output.writeUTF(username+": "+s.nextLine());
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            Thread writer = new Thread() {
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            System.out.println(input.readUTF());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            reader.start();
            writer.start();

            reader.join();
            writer.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
