import common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Socket socket;
    static ObjectEncoderOutputStream out;  // сериализует объект
    static ObjectDecoderInputStream in;   // десериализует объект

    static void start() {
        try{
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 100 * 100 * 1024);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void stop(){  // остановка
        try{
            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    static boolean sendMsg(AbstractMessage msg){   // отправить сообщение
        try{
            out.writeObject(msg);  // отправляет файл на сервер
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    static AbstractMessage readObject() throws  ClassNotFoundException, IOException {  // чтение объекта
        Object obj = in.readObject(); // получает файл с сервера
        return (AbstractMessage) obj;
    }
}
