import java.io.*;

public class IOStreamHelper {   
    public static String receiveResponse (InputStream inputStream) throws IOException {
        String response = "";
        int bytesRead;
        int cntEL = 0;
        while(true) {
            bytesRead = inputStream.read();
            if(bytesRead == '\n') {
                cntEL++;
            } else if(bytesRead == '\r') {}
            else {
                cntEL = 0;
            }
            response += (char)bytesRead;
            if(cntEL == 2) {
                break;
            }
        }
        return response;
    }

    public static void receiveResponse (InputStream inputStream, String savePath) throws IOException {
        int bytesRead;
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(savePath));
        byte[] buffer = new byte[1024];
        while((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
    public static void sendRequest (OutputStream outputStream, String request) throws IOException {
        outputStream.write(request.getBytes());
        outputStream.flush();
    }
}
