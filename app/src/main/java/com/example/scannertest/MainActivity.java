package com.example.scannertest;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.IDcsSdkApiDelegate;
import com.zebra.scannercontrol.SDKHandler;


import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements IDcsSdkApiDelegate {

    SDKHandler sdkHandler;
    ArrayList<DCSScannerInfo> mScannerInfoList=new ArrayList<>();
    static Dialog dialogPairNewScanner;
    static int connectedScannerID;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the SDK delegate to receive events
        sdkHandler = new SDKHandler(this);
        sdkHandler.dcssdkSetDelegate(this);
        sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);

        /////////////////////////////////////////////////////////////////////////////////////////////
        //이벤트 구독
        int notifications_mask = 0;
        // We would like to subscribe to all scanner available/not-available events
        notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value |
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value;
        // We would like to subscribe to all scanner connection events
        notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value |
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value;
        // We would like to subscribe to all barcode events
        notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value;
        // subscribe to events set in notification mask
        sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
        /////////////////////////////////////////////////////////////////////////////////////////////

        sdkHandler.dcssdkGetAvailableScannersList(mScannerInfoList);
//        if(!mScannerInfoList.isEmpty()) {
//            sdkHandler.dcssdkEstablishCommunicationSession(mScannerInfoList.get(0).getScannerID());
//        } 이거 아마 블루투스만 쓰는 것 같아
        sdkHandler.dcssdkEnableAvailableScannersDetection(true); //scanner enable
        if (sdkHandler != null) {
            mScannerInfoList.clear();
            sdkHandler.dcssdkGetAvailableScannersList(mScannerInfoList);
            sdkHandler.dcssdkGetActiveScannersList(mScannerInfoList);

            connectedScannerID = mScannerInfoList.get(0).getScannerID();
        }



        Button btnEnable = (Button) findViewById(R.id.enable);
        Button btnDisable = (Button) findViewById(R.id.disable);
        textView = findViewById(R.id.ScanData);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.enable) {
                    textView.append("enable button click\n");
                    sdkHandler.dcssdkEstablishCommunicationSession(mScannerInfoList.get(0).getScannerID());
                    enableScanner(v);

                } else if (id == R.id.disable) {
                    textView.append("disable button click\n");
                    sdkHandler.dcssdkTerminateCommunicationSession(mScannerInfoList.get(0).getScannerID());
                    disableScanner(v);
                }
            }
        };

        btnEnable.setOnClickListener(listener);
        btnDisable.setOnClickListener(listener);
        }


//        //////////////////////////////////////////////////////////////////
        private class MyAsyncTask extends AsyncTask<String,Integer,Boolean>{
            int scannerId;
            DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
            StringBuilder outXML;
            public MyAsyncTask(int scannerId,DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode,StringBuilder outXML){
                this.scannerId = scannerId;
                this.opcode = opcode;
                this.outXML = outXML;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                textView.append("Scanner ID: " + scannerId + " enabled successfully\n");
            } else {
                textView.append("Failed to enable Scanner ID: " + scannerId + "\n");
            }
        }

    @Override
            protected Boolean doInBackground(String... strings) {
                String inXML = strings[0];
                StringBuilder outXML = null;

                if (sdkHandler != null)
                {
                    if (outXML == null) {
                        outXML = new StringBuilder();
                    }
// calling execute command SDK API
                    DCSSDKDefs.DCSSDK_RESULT result =
                            sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(
                                    opcode,inXML,outXML,scannerId);
// return true if DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS
// false otherwise
                    if(result== DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS) {

                        return true;
                    }
                    else if(result==DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
                        return false;
                }
                return false;
            }
        }
    ////////////////////////////////////////////////////////////////////////
    private class DisableAsyncTask extends AsyncTask<String,Integer,Boolean>{
        int scannerId;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
        StringBuilder outXML;
        public DisableAsyncTask(int scannerId,DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode,StringBuilder outXML){
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.outXML = outXML;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                textView.append("Scanner ID: " + scannerId + " disabled successfully\n");
            } else {
                textView.append("Failed to disable Scanner ID: " + scannerId + "\n");
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String inXML = strings[0];
            StringBuilder outXML = null;

            if (sdkHandler != null)
            {
                if (outXML == null) {
                    outXML = new StringBuilder();
                }
// calling execute command SDK API
                DCSSDKDefs.DCSSDK_RESULT result =
                        sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(
                                opcode,inXML,outXML,scannerId);
// return true if DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS
// false otherwise
                if(result== DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS) {

                    return true;
                }
                else if(result==DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
                    return false;
            }
            return false;
        }
    }
//
public void enableScanner(View view) {
    String in_xml = "<inArgs><scannerID>" + connectedScannerID + "</scannerID></inArgs>";
    new MyAsyncTask (connectedScannerID,DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_ENABLE,null).
            execute(new String[]{in_xml});
}

    public void disableScanner(View view) {
        String in_xml = "<inArgs><scannerID>" + connectedScannerID + "</scannerID></inArgs>";
        new DisableAsyncTask (connectedScannerID,DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_DISABLE,null).
            execute(new String[]{in_xml});
    }


    @Override
    public void dcssdkEventScannerAppeared(DCSScannerInfo dcsScannerInfo) {
        mScannerInfoList.add(dcsScannerInfo);
        if (mScannerInfoList.size() > 0) {
            DCSScannerInfo reader = mScannerInfoList.get(0);
            textView.setText(reader.getScannerName());
        }
    }

    @Override
    public void dcssdkEventScannerDisappeared(int i) {

    }

    @Override
    public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo dcsScannerInfo) {
        connectedScannerID = dcsScannerInfo.getScannerID();
        textView.append("...Scanner ID : "+ connectedScannerID + " Connected\n");
    }

    @Override
    public void dcssdkEventCommunicationSessionTerminated(int i) {

    }

    //바코드 이벤트 처리
    @Override
    public void dcssdkEventBarcode(byte[] barcodeData, int barcodeType, int fromScannerID) {
        String code = new String(barcodeData);
        Barcode barcode = new Barcode(barcodeData,barcodeType,fromScannerID);
        textView.append(code+"\n");
    }

    @Override
    public void dcssdkEventImage(byte[] bytes, int i) {

    }

    @Override
    public void dcssdkEventVideo(byte[] bytes, int i) {

    }

    @Override
    public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {

    }


}
