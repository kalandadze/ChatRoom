import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User extends Thread{
    DataInputStream input;
    private Socket socket;
    private String username;
    DataOutputStream output;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket getSocket() {
        return socket;
    }

    public User(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String text) throws IOException {
        try {
            output.writeUTF(text);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void exit() throws IOException {
        send("exit");
        ChatRoom.removeUser(this);
        ChatRoom.receive(username+" left the chat",socket);
    }
    public void changeNickName() throws IOException {
        send("current username - "+username);
        send("enter new username");
        String oldUsername=username;
        setUsername(input.readUTF());
        ChatRoom.receive(oldUsername + " changed his username to "+ username,socket);
    }
    public void privateMessage() throws IOException {
        for (int i=0;i<ChatRoom.getUsers().size();i++){
            send((i+1)+". "+ChatRoom.getUsers().get(i).getUsername());
        }
        send("enter id of who you want to message");
        int a=Integer.parseInt(input.readUTF());
        send("type the private message");
        ChatRoom.sendPrivately(input.readUTF(),this,ChatRoom.getUsers().get(a-1));
    }
    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                String inp=input.readUTF();
                if (inp.equalsIgnoreCase("\\help")){
                    Thread help= new Thread(() -> {
                        boolean bool=true;
                        while (bool){
                            try {
                                send(" \n=====================================================\n"+
                                            username+
                                            "\ntype \"1\" to send private message to someone\n"+
                                            "type \"2\" to change nickname\n"+
                                            "type \"3\" to continue messaging\n"+
                                            "type \"4\" to exit programme\n"+
                                            "Number of active users:" + new ChatRoom().getUsersNum() +
                                            "\n=====================================================\n");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            String a;
                            try {
                                a=input.readUTF();
                                System.out.println(a);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            switch (a){
                                case "1":
                                    try {
                                        privateMessage();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                case "2":
                                    try {
                                        changeNickName();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                case "3":
                                    try {
                                        send("\n");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    bool=false;
                                    break;
                                case "4":
                                    try {
                                        exit();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                default:
                                    try {
                                        send("invalid input");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                            }
                        }
                    });
                    help.start();
                    help.join();
                }else {
                    ChatRoom.receive(username+": "+inp, socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
