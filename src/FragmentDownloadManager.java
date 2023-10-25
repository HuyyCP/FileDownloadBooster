import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLOutput;

public class FragmentDownloadManager {
    URL url; // url chua file, xu li redirect
    String responseCode; // response code
    public FragmentDownloadManager(String urlStr) throws MalformedURLException {
        this.url = new URL(urlStr);
        this.responseCode = ""; //empty response code
    }
    public void HandleRedirectURL() {
        do {
            try {
                int port;
                if(url.getProtocol().equals("https")) { port = 443;}
                else { port = 80;}
                Socket socket = new Socket(url.getHost(), port);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStreeam = socket.getOutputStream();
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
                outputStreeam.write(request.getBytes());
                outputStreeam.flush();
                String response = "";
                int bytesRead, cntEL = 0;
                do {
                    bytesRead = inputStream.read();
                    if (bytesRead == '\n') {
                        cntEL++;
                    } else if (bytesRead == '\r') {
                    } else {
                        cntEL = 0;
                    }
                    response += (char) bytesRead;
                } while (cntEL != 2);
                System.out.println(response);
                inputStream.close();
                outputStreeam.close();
                socket.close();


                responseCode = response.substring(9, response.indexOf(" ", 9));
                System.out.println("responseCode = " + responseCode);
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


}
