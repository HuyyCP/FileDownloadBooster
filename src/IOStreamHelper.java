import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IOStreamHelper {   
    public static Map<String,String> receiveHeader (InputStream inputStream) throws IOException {
        String responseLine = "";
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
            responseLine += (char)bytesRead;
            if(cntEL == 2) {
                break;
            }
        }

        responseLine = "Status: " + responseLine;
        String[] headers = responseLine.split("\r\n");
        Map<String, String> responseHeaders = new HashMap<>();
        for(String header : headers) {
            String[] keyval = header.split(": ");
            responseHeaders.put(keyval[0], keyval[1]);
        }
//        System.out.println("Response: " + responseHeaders + "\n");
        return responseHeaders;
    }

    public static void receiveBody (BufferedInputStream inputStream, BufferedOutputStream outputStream) throws IOException {
        int bytesRead;
        int bufferSize = 4096 * 4; // multiple of 4KB
        byte[] buffer = new byte[bufferSize];
        while((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
//        System.out.println("Available: " + inputStream.available());
//        while(inputStream.available() > 0) {
//            bytesRead = inputStream.read(buffer, 0, Math.min(inputStream.available(), bufferSize));
//            System.out.println(bytesRead);
//            outputStream.write(buffer, 0, bytesRead);
//        }
    }
    public static long receiveBody(BufferedInputStream inputStream, RandomAccessFile file, long offset) throws IOException {
        int bufferSize = 4096 * 4;
        byte[] buffer = new byte[bufferSize];
        int bytesRead; long totalBytesRead = 0;
        file.seek(offset);
        while((bytesRead = inputStream.read(buffer)) != -1) {
            file.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }
        inputStream.close();
        file.close();
        return totalBytesRead;
    }
    public static void sendRequest (OutputStream outputStream, String request) throws IOException {
//        System.out.println("Request: " + request);
        outputStream.write(request.getBytes());
        outputStream.flush();
    }
}
