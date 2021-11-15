package com.revature.assessors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

        private List <String> fileList;
        public static final String OUTPUT_ZIP_FILE = "Folder.zip";
        public static  String SOURCE_FOLDER = "nope"; // SourceFolder path

        public ZipUtils() {
            fileList = new ArrayList < String > ();
        }

        public static void main(String[] args) {
            ZipUtils appZip = new ZipUtils();
            appZip.generateFileList(new File(SOURCE_FOLDER));
            appZip.zipIt(OUTPUT_ZIP_FILE);
        }

        public void zipIt(String zipFile) {
            byte[] buffer = new byte[1024];
            String source = new File(SOURCE_FOLDER).getName();
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(zipFile);
                zos = new ZipOutputStream(fos);

                //System.out.println("Output to Zip : " + zipFile);
                FileInputStream in = null;

                for (String file: this.fileList) {
                    //System.out.println("File Added : " + file);
                    ZipEntry ze = new ZipEntry(source + File.separator + file);
                    zos.putNextEntry(ze);
                    try {
                        in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                        int len;
                        while ((len = in .read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    } finally {
                        in.close();
                    }
                }

                zos.closeEntry();
                //System.out.println("Folder successfully compressed");

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void generateFileList(File node) {
            // add file only
            if (node.isFile()) {
                fileList.add(generateZipEntry(node.toString()));
            }

            if (node.isDirectory()) {
                String[] subNote = node.list();
                for (String filename: subNote) {
                    generateFileList(new File(node, filename));
                }
            }
        }

        private String generateZipEntry(String file) {
            return file.substring(SOURCE_FOLDER.length() + 1, file.length());
        }

        public void deleteZip(){
            File zip = new File(OUTPUT_ZIP_FILE);
            zip.delete();
        }

        public static String base64SourceCode(){
            Path path = Paths.get(OUTPUT_ZIP_FILE);
            try {
                byte[] data = Files.readAllBytes(path);
                String encoded = Base64.getEncoder().encodeToString(data);
                return encoded;
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

    }

