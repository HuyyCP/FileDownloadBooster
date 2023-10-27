import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLOutput;

public class FragmentDownloadManager {
    URL url; // url chua file, xu li redirect

    public FragmentDownloadManager(String urlStr) throws MalformedURLException {
        this.url = new URL(urlStr);
    }
    public void HandleRedirectURL() {
        String responseCode = ""; // response code
        do {
            try {
                Socket socket;
                if(url.getProtocol().equals("https")) {
                    socket = SSLSocketFactory.getDefault().createSocket(url.getHost(), 443);
                } else {
                    socket = new Socket(url.getHost(), 80);
                }
                BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

                String request;

                if(responseCode.equals("303")) {
                    String username = "102210105";
                    String password = "Svien@21";
                    request = "POST " + url.getPath() + " HTTP/1.1\r\n" +
                            "Host: " + url.getHost() + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded\r\n" +
                            "Content-length: " + (username.length() + password.length() + 19) + "\r\n\r\n" +
                            "username=" + username + "&password=" + password;
                } else {
                    request = "HEAD " + url.getPath() + " HTTP/1.1\r\n" +
                            "Host: " + url.getHost() + "\r\n\r\n";
                }
                System.out.println(request);
                IOStreamHelper.sendRequest(outputStream, request);
                String response = IOStreamHelper.receiveResponse(inputStream);
                System.out.println(response);
                inputStream.close();
                outputStream.close();
                socket.close();


                responseCode = response.substring(9, response.indexOf(" ", 9));
//                System.out.println("responseCode = " + responseCode);
                if(responseCode.equals("200")) { break;}
                else if(responseCode.startsWith("3")) {
                    int pos = response.indexOf("Location");
                    String location = response.substring(pos + 10, response.indexOf("\r\n", pos));
                    url = new URL(location);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } while(true);

    }

    public void downloadFile(String savePath) throws IOException {
        // Handle string URL
        String protocol = url.getProtocol(); // get prototcol
        String host = url.getHost(); // get host
        String path = url.getPath(); // get path
        String filename = path.substring(path.lastIndexOf('/') + 1); // get file name
        savePath += filename; // add file name to directory

        // Create a socket connection
        try (Socket socket = protocol.equals("https") ? SSLSocketFactory.getDefault().createSocket(host, 443) : new Socket(host, 80);
             BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream()))
        {
            // Create a http HEAD request
            String headRequest = "HEAD " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";

            // Send request to server
            IOStreamHelper.sendRequest(outputStream, headRequest);

            // Read response from HEAD request
            String headResponse = IOStreamHelper.receiveResponse(inputStream);
            System.out.println(headResponse);


            // Create a http GET request
            String request = "GET " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";

            // Send request to server
            IOStreamHelper.sendRequest(outputStream, request);

            // Read response from GET request and save the file
            IOStreamHelper.receiveResponse(inputStream, savePath);

            System.out.println("File has been successfully downloaded.");

        } catch (IOException exception) {

        }
    }
}
