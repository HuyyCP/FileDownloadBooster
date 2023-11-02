import java.io.*;

public class FileDownloadBooster {
    public static void main(String[] args) {
        String httpURL = "http://www.pdf995.com/samples/pdf.pdf"; // pass
        String lmsURL = "http://lms.dut.udn.vn/pluginfile.php/188023/mod_resource/content/1/Modern%20Operating%20Systems%204th%20Edition--Andrew%20Tanenbaum.pdf"; // code 303
        String httpsURL = "https://www.k12blueprint.com/sites/default/files/Learning-Management-System-Guide.pdf"; // pass
        String tmpURL = "https://cuuduongthancong.com/dlf/2334741/cau-truc-du-lieu-va-giai-thuat/pham-the-bao/slides---cay.pdf";
        String imgURL = "https://cuuduongthancong.com/dlf/102433/he-dieu-hanh/2012---2013-2.jpg";
        String localhostURL = "http://localhost/MVCExample/index.html";
        String savePath = "D:/PBL_Storage/";
        try {
            FragmentDownloadManager manager  = new FragmentDownloadManager(httpsURL);
            manager.downloadFile(savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

