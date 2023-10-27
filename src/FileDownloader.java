import java.io.*;

public class FileDownloader {
    public static void main(String[] args) {
        String httpURL = "http://www.pdf995.com/samples/pdf.pdf";
        String lmsURL = "http://lms.dut.udn.vn/pluginfile.php/188023/mod_resource/content/1/Modern%20Operating%20Systems%204th%20Edition--Andrew%20Tanenbaum.pdf";
        String httpsURL = "https://www.k12blueprint.com/sites/default/files/Learning-Management-System-Guide.pdf";
        String tmpURL = "https://cuuduongthancong.com/dlf/2334741/cau-truc-du-lieu-va-giai-thuat/pham-the-bao/slides---cay.pdf"; // 403 forbidden
        String savePath = "D:/PBL_Storage/";
        try {
            FragmentDownloadManager manager  = new FragmentDownloadManager(httpsURL);
            manager.downloadFile(savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

