package BLL;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IOStreamHelper {
    final static int bufferSize = 4096 * 4; // multiple of 4KB
    public static Map<String,String> receiveHeader (InputStream inputStream) throws IOException {
        StringBuilder responseLine = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//        while((line = reader.readLine()) != null) {
//            if(line.trim().isEmpty()) {
//                break;
//            }
//            responseLine.append(line).append("\r\n");
//        }

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
            responseLine.append((char) bytesRead);
            if(cntEL == 2) {
                break;
            }
        }

        responseLine.insert(0, "Status: ");
        String[] headers = responseLine.toString().split("\r\n");
        Map<String, String> responseHeaders = new HashMap<>();
        for(String header : headers) {
            if(!header.trim().isEmpty()) {
                String[] keyval = header.split(": ");
                responseHeaders.put(keyval[0].toLowerCase(), keyval[1]);
            }
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
