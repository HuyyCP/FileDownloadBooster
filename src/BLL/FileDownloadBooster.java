package BLL;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class FileDownloadBooster {
    public static void main(String[] args) {
        String httpURL = "http://www.pdf995.com/samples/pdf.pdf"; // pass
        String lmsURL = "http://lms.dut.udn.vn/pluginfile.php/188023/mod_resource/content/1/Modern%20Operating%20Systems%204th%20Edition--Andrew%20Tanenbaum.pdf"; // code 303
        String httpsURL = "https://www.k12blueprint.com/sites/default/files/Learning-Management-System-Guide.pdf"; // pass
        String tmpURL = "https://cuuduongthancong.com/dlf/2334741/cau-truc-du-lieu-va-giai-thuat/pham-the-bao/slides---cay.pdf";
        String imgURL = "https://sample-videos.com/img/Sample-jpg-image-30mb.jpg"; // pass
        String docURL = "https://sample-videos.com/doc/Sample-doc-file-5000kb.doc"; // pass
        String pptURL = "https://sample-videos.com/ppt/Sample-PPT-File-1000kb.ppt"; // pass
        String zipURL = "https://sample-videos.com/zip/100mb.zip"; // pass (12s / 10s edge)
        String xlsURL = "https://sample-videos.com/xls/Sample-Spreadsheet-50000-rows.xls"; // pass
        String csvURL = "https://sample-videos.com/csv/Sample-Spreadsheet-500000-rows.csv"; // pass
        String mp4URL = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4"; // pass
        String flvURL = "https://sample-videos.com/video123/flv/720/big_buck_bunny_720p_30mb.flv"; // pass
        String exeURL = "https://get.videolan.org/vlc/3.0.20/win64/vlc-3.0.20-win64.exe"; // pass
        String pngURL = "https://sample-videos.com/img/Sample-png-image-30mb.png"; // pass
        String gifURL = "https://sample-videos.com/gif/2.gif"; // pass
        String mp3URL = "https://sample-videos.com/audio/mp3/wave.mp3"; // pass
        String zipURLpro = "http://212.183.159.230/1GB.zip"; // pass (6mb/s 5p), test lan 2: 3p23s

        String savePath = "D:\\PBL_Storage\\";
        try {
            FileDownloadManager manager  = new FileDownloadManager();
            FileDownloader fileDownloader = new FileDownloader(1, new URL(imgURL), new File(savePath));
            manager.downloadFile(fileDownloader);
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL");
        }
    }
}

