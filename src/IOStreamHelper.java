import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IOStreamHelper {
    final static int bufferSize = 4096; // multiple of 4KB
    public static Map<String,String> receiveHeader (InputStream inputStream) throws IOException {
        String responseLine = "";
        int bytesRead;
        byte[] buffer = new byte[bufferSize];
        do {
            bytesRead = inputStream.read(buffer);
            responseLine += new String(buffer, 0, bytesRead);
        } while (!responseLine.contains("\r\n\r\n") && bytesRead != -1);

        responseLine = "Status: " + responseLine;
        String[] headers = responseLine.split("\r\n");
        Map<String, String> responseHeaders = new HashMap<>();
        for(String header : headers) {
            String[] keyval = header.split(": ");
            responseHeaders.put(keyval[0].toLowerCase(), keyval[1]);
        }
        return responseHeaders;
    }

    public static void receiveBody (BufferedInputStream inputStream, BufferedOutputStream outputStream) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[bufferSize];
        while((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
    public static long receiveBody(BufferedInputStream inputStream, RandomAccessFile file, long offset, long length) throws IOException {
        receiveHeader(inputStream); // skip header

        byte[] buffer = new byte[bufferSize];
        long totalBytesRead = 0;
        int bytesRead;
        file.seek(offset);
        while(totalBytesRead < length) {
            bytesRead = inputStream.read(buffer);
            file.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }
        inputStream.close();
        file.close();
        return totalBytesRead;
    }
    public static void sendRequest (OutputStream outputStream, String request) throws IOException {
        outputStream.write(request.getBytes());
        outputStream.flush();
    }
}
