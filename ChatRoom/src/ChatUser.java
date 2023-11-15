import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatUser {
    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
        System.out.print("UserName: ");
        String username=s.nextLine();
        Socket socket;
        while (true){
            System.out.print("Address: ");
            String address = s.next();
            System.out.print("port: ");
            int port = s.nextInt();

            try {
                socket = new Socket(address, port);
                System.out.println("Connected to " + address + ":" + port);
                new DataOutputStream(socket.getOutputStream()).writeUTF(username);
                break;
            } catch (IOException e) {
                System.out.println("\nport or address is incorrect\ntry again!\n");
            }
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
                            output.writeUTF(s.nextLine());
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
                            String inp= input.readUTF();
                            if (inp.equals("exit")){
                                reader.interrupt();
                                break;
                            }
                            System.out.println(inp);
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
