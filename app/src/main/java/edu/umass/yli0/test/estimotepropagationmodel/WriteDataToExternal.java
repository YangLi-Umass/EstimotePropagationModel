package edu.umass.yli0.test.estimotepropagationmodel;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by li on 10/18/2015.
 */
public class WriteDataToExternal {

    WriteDataToExternal(String _appName, String _fileName, File _file){
        appName = _appName;
        fileName = _fileName;
        sdCard = _file;
    }

    String appName;
    String fileName;
    File sdCard;
    OutputStreamWriter osw;

    void open(){
        //File sdCard = environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/" + appName);
        directory.mkdirs();

        File file = new File(directory, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            osw = new OutputStreamWriter(outputStream);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write(String _toWrite){
        try{
            osw.write(_toWrite);
            osw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void close(){
        try{
            osw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
