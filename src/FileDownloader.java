import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;

public class FileDownloader {
    public static void main(String[] args) {
        String httpURL = "http://www.pdf995.com/samples/pdf.pdf";
        String httpsURL = "https://www.k12blueprint.com/sites/default/files/Learning-Management-System-Guide.pdf";
        String savePath = "D:/";
        try {
            downloadFile(httpURL, savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileUrl, String savePath) throws IOException {
        // Handle string URL
        URL url = new URL(fileUrl);
        String protocol = url.getProtocol(); // get prototcol
        String host = url.getHost(); // get host
        String path = url.getPath(); // get path
        String filename = path.substring(path.lastIndexOf('/') + 1); // get file name
        savePath += filename; // add file name to directory

        // Create a socket connection
        try (Socket socket = protocol.equals("https") ? SSLSocketFactory.getDefault().createSocket(host, 443) : new Socket(host, 80);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(savePath));
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream())
        {
            // Create a http HEAD request
            String headRequest = "HEAD " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";

            // Send request to server
            outputStream.write(headRequest.getBytes());
            outputStream.flush();

            // Read response from HEAD request
            String headResponse = "";
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
                headResponse += (char)bytesRead;
                if(cntEL == 2) {
                    break;
                }
            }
            System.out.println(headResponse);

            // Create a http GET request
            String request = "GET " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";

            // Send request to server
            outputStream.write(request.getBytes());
            outputStream.flush();

            // Read response from GET request and save the file
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File has been successfully downloaded.");

        } catch (IOException exception) {

        }
    }
}

