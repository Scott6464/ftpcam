package com.example.android.ftpcam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;
import static java.lang.System.in;

/**
 * Created by Scott on 10/31/2017.
 */

public class ftpUtility {

    //String host = "72.238.5.220";
    String host  = "192.168.1.144";
    Integer port = 1024;
    String username = "sengle64";
    String password = "crouton";
    FTPClient mFTPClient = null;

    // Now, declare a public FTP client object.

    private static final String TAG = "MyFTPClientFunctions";

    // Method to connect to FTP server:
    public boolean ftpConnect() {
        try {
            mFTPClient = new FTPClient();
            mFTPClient.connect(host, port);
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                boolean status = mFTPClient.login(username, password);

				/*
                 * Set File Transfer Mode
				 *
				 * To avoid corruption issue you must specified a correct
				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
				 * transferring text, image, and compressed files.
				 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                Log.i(TAG, "Connected");
                return status;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: could not connect to host " + host);
        }

        return false;
    }

    // Method to disconnect from FTP server:

    public boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while disconnecting from ftp server.");
        }

        return false;
    }

    // Method to get current working directory:

    public String ftpGetCurrentWorkingDirectory() {
        try {
            String workingDir = mFTPClient.printWorkingDirectory();
            return workingDir;
        } catch (Exception e) {
            Log.e(TAG, "Error: could not get current working directory.");
        }

        return null;
    }

    // Method to change working directory:

    public boolean ftpChangeDirectory(String directory_path) {
        try {
            mFTPClient.changeWorkingDirectory(directory_path);
        } catch (Exception e) {
            Log.e(TAG, "Error: could not change directory to " + directory_path);
        }
        return false;
    }

    // Method to list all files in a directory:

    public List<String> ftpPrintFilesList(String dir_path) {
        List<String> fileList = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;
            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();
                if (isFile) {
                    fileList.add(name);
                    Log.i(TAG, "File : " + name);
                } else {
                    //fileList[i] = "Directory :: " + name;
                    //Log.i(TAG, "Directory : " + name);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
    }


    // Method to list all directories:

    public List<String> ftpPrintDirectoryList(String dir_path) {
        List<String> fileList = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;
            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    //fileList[i] = name;
                    //Log.i(TAG, "File : " + name);
                } else {
                    fileList.add(name);
                    Log.i(TAG, "Directory : " + name);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
    }





    // Method to create new directory:

    public boolean ftpMakeDirectory(String new_dir_path) {
        try {
            boolean status = mFTPClient.makeDirectory(new_dir_path);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Error: could not create new directory named "
                    + new_dir_path);
        }

        return false;
    }

    // Method to delete/remove a directory:

    public boolean ftpRemoveDirectory(String dir_path) {
        try {
            boolean status = mFTPClient.removeDirectory(dir_path);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Error: could not remove directory named " + dir_path);
        }

        return false;
    }

    // Method to delete a file:

    public boolean ftpRemoveFile(String filePath) {
        try {
            boolean status = mFTPClient.deleteFile(filePath);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Method to rename a file:

    public boolean ftpRenameFile(String from, String to) {
        try {
            boolean status = mFTPClient.rename(from, to);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Could not rename file: " + from + " to: " + to);
        }

        return false;
    }

    // Method to download a file from FTP server:

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: path to the source file in FTP server desFilePath: path to
     * the destination file to be saved in sdcard
     */

    public byte[] ftpDownloadStream(String filename) throws IOException {
        try {
            InputStream in = mFTPClient.retrieveFileStream(filename);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        BufferedInputStream inbf = new BufferedInputStream(in);
        byte buffer[] = new byte[1024];
        int readCount;
        byte result[] = null;
        int length = 0;
        while ((readCount = inbf.read(buffer)) > 0) {
            int preLength = length;
            length += readCount;
            byte temp[] = new byte[result.length];
            result = new byte[length];
            System.arraycopy(temp, 0, result, 0, temp.length);
            System.arraycopy(buffer, 0, result, preLength, readCount);
        }
        return result;
    }



    public boolean ftpDownload(String srcFilePath, File desFilePath) {
        boolean status = false;
        try {
            FileOutputStream desFileStream = new FileOutputStream(desFilePath);
            status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();
            Log.i(TAG, "Download: " + srcFilePath + " " + String.valueOf(status));
            return status;
        } catch (Exception e) {
            Log.e(TAG, "download failed " + e.toString());
        }
        return status;
    }

    // Method to upload a file to FTP server:

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: source file path in sdcard desFileName: file name to be
     * stored in FTP server desDirectory: directory path where the file should
     * be upload to
     */
    public boolean ftpUpload(String srcFilePath, String desFileName,
                             String desDirectory, Context context) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);

            // change working directory to the destination directory
            // if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            // }

            srcFileStream.close();

            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "upload failed: " + e);
        }

        return status;
    }
}


