package com.possible.mecash.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class Logging {
    @Value("${path.logging}")
    private String logFile;

    @Async
    public void writeLog(String source, String request, String response, Exception err){
        String fileName = "meCashApp.log";
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            Date todaysDate = dateFormat.parse(dateFormat.format(new Date()));
            String onDate = dateFormat.format(new Date());

            File dir = new File(logFile);
            if (!dir.exists()){
                dir.mkdir();
            }

            fw = new FileWriter(logFile + onDate.split(" ")[0] + "_"+ fileName, true);
            bw = new BufferedWriter(fw);
            bw.write("\n");
            bw.write("DateTime : ".concat(todaysDate.toString()) + "\n");
            bw.write("Source : ".concat(source) + "\n");
            bw.write("Request : ".concat(request) + "\n");
            bw.write("Response : ".concat(response) + "\n");

            if (err != null){
                bw.write("Exception : ".concat(err.toString()) + "\n");
                if (err.getCause() != null){
                    String cause = err.getCause().getCause().toString();
                    bw.write("Exception Cause: ".concat(cause) + "\n");
                    bw.write("Exception Message: ".concat(err.getMessage()) + "\n");
                }
            }
            bw.write("\n");
        }
        catch (IOException | ParseException e){
            log.error("IO-err:: {}", e.getMessage());
        }
        finally {
            try{
                if (bw != null){
                    bw.close();
                    fw.close();
                }
            } catch (IOException e){
                log.error("IO-err:: {}", e.getMessage());
            }
        }
    }
}
