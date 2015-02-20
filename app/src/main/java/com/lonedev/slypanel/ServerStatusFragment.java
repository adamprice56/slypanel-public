package com.lonedev.slypanel;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.Random;


/**
 * Created by root on 02/10/14.
 */
public class ServerStatusFragment extends Fragment {

    public static ProgressBar cpuPercentage;
    public static ProgressBar ramPercentage;
    public static ProgressBar tempValue;
    public static XYPlot cpuGraph;
    public static SimpleXYSeries cpuGraphSeries;
    public static XYPlot ramGraph;
    public static SimpleXYSeries ramGraphSeries;
    public static XYPlot tempGraph;
    public static SimpleXYSeries tempGraphSeries;

    public static GridLayout cpuGraphLayout;
    public static GridLayout ramGraphLayout;
    public static GridLayout tempGraphLayout;

    static String sshPassword = "nullPassword";

    static String cpuReplyLine = null;
    static String ramReplyLine = null;
    static String tempReplyLine = null;

    Double cpuint = 0.0;
    Double ramint = (double) randInt(0, 100);
    Double tempint = (double) randInt(20, 100);

    public ServerStatusFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ServerStatusView = inflater.inflate(R.layout.fragment_server_status, container, false);
        sshConnection.testing = false;

        return (ServerStatusView);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        cpuPercentage = (ProgressBar) view.findViewById(R.id.cpuUsageBar);
        cpuGraphLayout = (GridLayout) view.findViewById(R.id.gridLayoutCpu);
        ramPercentage = (ProgressBar) view.findViewById(R.id.ramUsageBar);
        ramGraphLayout = (GridLayout) view.findViewById(R.id.gridLayoutRam);
        tempValue = (ProgressBar) view.findViewById(R.id.tempBar);
        tempGraphLayout = (GridLayout) view.findViewById(R.id.gridLayoutTemp);


        cpuGraph = (XYPlot) view.findViewById(R.id.cpuGraph);
        cpuGraphSeries = new SimpleXYSeries("CPU Usage");
        setupCpuGraph();

        ramGraph = (XYPlot) view.findViewById(R.id.ramGraph);
        ramGraphSeries = new SimpleXYSeries("RAM Usage");
        setupRamGraph();

        tempGraph = (XYPlot) view.findViewById(R.id.tempGraph);
        tempGraphSeries = new SimpleXYSeries("Temperature");
        setupTempGraph();

        //Set up Connection Fragment
//
    }

    //For testing -- Replace when needed
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


        //CPU Percentage command - "top -b -n2 | grep \"Cpu(s)\" | awk '{print $2 + $4}' | tail -1"
        //RAM Percentage command - "top -bn1 | awk '/Mem/ { mem = "" $5 / $3 * 100 "" } END { print mem }'"
        //Temperature command - sensors... something

        //Temperature - REQUIRES lm-sensors
        //sudo sensors-detect needed

    static void update() {
        String cpuCommand = "top -b -n2 | grep \"Cpu(s)\" | awk '{print $2 + $4}' | tail -1 && Done";
        String ramCommand = "free | awk 'FNR == 3 {print $3/($3+$4)*100}'";
        String tempCommand = "echo 40.0";

        cpuReplyLine = null;
        ramReplyLine = null;
        tempReplyLine = null;

      sshService cpuTask = new sshService();
      sshService ramTask =  new sshService();
      sshService tempTask =  new sshService();


        cpuTask.executeOnExecutor(sshService.THREAD_POOL_EXECUTOR, cpuCommand);
        ramTask.executeOnExecutor(sshService.THREAD_POOL_EXECUTOR, ramCommand);
        tempTask.executeOnExecutor(sshService.THREAD_POOL_EXECUTOR, tempCommand);
    }



    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void updateCpuGraph(String cpuReplyLine) {
        try {
            cpuint = Double.valueOf(cpuReplyLine);

            cpuPercentage.setProgress((int) Math.floor(cpuint));
            cpuGraphSeries.addLast(null, cpuint);
            Log.w("Plot", "Plotted CPU " + cpuint);

            if (cpuGraphSeries.size() > 31) {
                cpuGraphSeries.removeFirst();
            }

            cpuGraph.redraw();
        } catch (NumberFormatException Exception) {
            Log.w("Error", "Unknown response. Incorrect command.");
        }
    }

    void updateRamGraph(String ramReplyLine) {
        try {
            ramint = Double.valueOf(ramReplyLine);

            ramPercentage.setProgress((int) Math.floor(ramint));
            ramGraphSeries.addLast(null, ramint);
            Log.w("Plot", "Plotted RAM " + ramint);


            if (ramGraphSeries.size() > 31) {
                ramGraphSeries.removeFirst();
            }
            ramGraph.redraw();
        } catch (NumberFormatException Exception) {
            Log.w("Error", "Unknown response. Incorrect command.");
        }

    }

    void updateTempGraph(String tempReplyLine) {
        try {

            tempint = Double.valueOf(tempReplyLine);

            tempValue.setProgress((int) Math.floor(tempint));
            tempGraphSeries.addLast(null, tempint);
            Log.w("Plot", "Plotted temp " + tempint);



            if (tempGraphSeries.size() > 31) {
                tempGraphSeries.removeFirst();
            }


            tempGraph.redraw();
        } catch (NumberFormatException Exception) {
            Log.w("Error", "Unknown response. Incorrect command.");
        }
    }


    void setupCpuGraph() {
        cpuGraphSeries.useImplicitXVals();
        cpuGraph.addSeries(cpuGraphSeries, new LineAndPointFormatter(Color.GREEN, Color.RED, Color.TRANSPARENT, null));

        cpuGraph.setDomainLabel("Time (Seconds)");
        cpuGraph.setDomainStepValue(5);
        cpuGraph.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
        cpuGraph.getDomainLabelWidget().pack();

        cpuGraph.setRangeLabel("Usage (%)");
        cpuGraph.setRangeStepValue(10);
        cpuGraph.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        cpuGraph.getRangeLabelWidget().pack();

        cpuGraph.setGridPadding(1, 1, 1, 1);
    }

    void setupRamGraph() {
        ramGraphSeries.useImplicitXVals();
        ramGraph.addSeries(ramGraphSeries, new LineAndPointFormatter(Color.GREEN, Color.RED, Color.TRANSPARENT, null));

        ramGraph.setDomainLabel("Time (Seconds)");
        ramGraph.setDomainStepValue(5);
        ramGraph.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
        ramGraph.getDomainLabelWidget().pack();

        ramGraph.setRangeLabel("Usage (%)");
        ramGraph.setRangeStepValue(10);
        ramGraph.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        ramGraph.getRangeLabelWidget().pack();

        ramGraph.setGridPadding(1, 1, 1, 1);
    }

    void setupTempGraph() {
        tempGraphSeries.useImplicitXVals();
        tempGraph.addSeries(tempGraphSeries, new LineAndPointFormatter(Color.GREEN, Color.RED, Color.TRANSPARENT, null));

        tempGraph.setDomainLabel("Time (Seconds)");
        tempGraph.setDomainStepValue(5);
        tempGraph.setDomainBoundaries(0, 30, BoundaryMode.FIXED);
        tempGraph.getDomainLabelWidget().pack();

        tempGraph.setRangeLabel("Temperature (Degrees)");
        tempGraph.setRangeStepValue(10);
        tempGraph.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        tempGraph.getRangeLabelWidget().pack();

        tempGraph.setGridPadding(1, 1, 1, 1);
    }

    void setVisibility(String graph, boolean visible) {
        switch (graph) {
            case "cpu":
                if (visible) {
                    cpuGraphLayout.setVisibility(View.VISIBLE);
                } else {
                    cpuGraphLayout.setVisibility(View.GONE);
                }
            case "ram":
                if (visible) {
                    ramGraphLayout.setVisibility(View.VISIBLE);
                } else {
                    ramGraphLayout.setVisibility(View.GONE);
                }
            case "temp":
                if (visible) {
                    tempGraphLayout.setVisibility(View.VISIBLE);
                } else {
                    tempGraphLayout.setVisibility(View.GONE);
                }
        }

    }
}



            //SSHManager Code
//    if (fileMan == null) {
//        fileMan = new FileManager();
//    }
//
//
//    if (sshHost == "not.an.ip.address") {
//        sshUsername = fileMan.loadDetails("username");
//        sshHost = fileMan.loadDetails("host");
//
//        if (sshHost == "not.an.ip.address") {
//            Log.w("Error", "Failed to retrieve settings");

//        }
//    }
//
//    if (sshPassword == "nullPassword") {
//        if (sshPassword == "nullPassword") {
//            Log.w("Error", "Failed to get password");

//        }
//    }
//
//    sshSession = jschManager.getSession(sshUsername, sshHost, port);
//    Log.w("SSH", "Username: " + sshUsername + " Host: " + sshHost + " Port: " + port);
//    Log.w("PASSWORD", sshPassword);
//    sshSession.setPassword(sshPassword);
//    sshSession.setConfig("StrictHostKeyChecking", "no");
//
//
//    sshSession.connect(600);
//
//    Channel sshChannel = sshSession.openChannel("exec");
//    if (sshSendCommand == "fakecommand") {
//        try {
//            // Try to set the command
//
//            if (sshSendCommand == "fakecommand") ;
//            {
//                // If the command fails to set, Throw any exception
//                throw new NoSuchMethodError();
//            }
//        } catch (NoSuchMethodError Exception) {
//            // Send a pre set command instead
//            Log.w("Error", "Unable to set a command");
//            sshSendCommand = "echo There was no command";
//            statusMessage = "There was no command!";

//        }
//    }
//    ChannelExec cExec = (ChannelExec) sshChannel;
//    replyFromServer = new BufferedReader(new InputStreamReader(cExec.getInputStream()));
////                    sshSendCommand += " && echo \"" + terminator + "\"";
//    cExec.setCommand(sshSendCommand);
//
//    sshChannel.setInputStream(null);
//    ((ChannelExec) sshChannel).setErrStream(System.err);
//
//    InputStream response = sshChannel.getInputStream();
//    sshChannel.connect();
//    cExec.connect();
//
//    Log.w("Status", "Command: " + sshSendCommand);
//
//    byte[] tmp = new byte[1024];
//    while (true) {
//        while (response.available() > 0) {
//            int i = response.read(tmp, 0, 1024);
//            // i = how many lines are left to read
//            // tmp = lines read
//            if (i < 0) break;
////                            reply[i] = (new String(tmp, 0, i));
//            replyLine = (new String(tmp, 0, i));
//            Log.w("Reply", "Final value: " + replyLine);
//        }
//
//
//        // Disconnect from session after sending the command?
////                    cExec.disconnect();
////                    sshSession.disconnect();
////                    MainActivity.connected = false;
//
//    }
//
//} catch (JSchException jschX) {
//        Log.w("Error", "Couldn't create the channel");
//        Log.w("Status", System.err.toString());
//        Log.w("JschException", jschX);
//
//        if (jschX.toString().contains("timeout")) {
//        Log.w("Error", "IP Address is incorrect");

//        }
//
//        if (jschX.toString().contains("Auth fail")) {
//        Log.w("Error", "Username/Password Incorrect");

//        }
//        if (jschX.toString().contains("socket")) {
//        sshSession.disconnect();
//        }
//        } catch (Exception Exception) {
//        Log.w("Exception", Exception);
//        }
//