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
        notifications_mask |=
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value |
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value;
        // We would like to subscribe to all scanner connection events
        notifications_mask |=
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value |
                        DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value;
        // We would like to subscribe to all barcode events
        notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value;
        // subscribe to events set in notification mask
        sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
        /////////////////////////////////////////////////////////////////////////////////////////////


        sdkHandler.dcssdkEnableAvailableScannersDetection(true); //scanner enable
        if (sdkHandler != null) {
            mScannerInfoList.clear();
            sdkHandler.dcssdkGetAvailableScannersList(mScannerInfoList);
            sdkHandler.dcssdkGetActiveScannersList(mScannerInfoList);
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

                } else if (id == R.id.disable) {
                    textView.append("disable button click\n");

                    //disableScanner(v);
                }
            }
        };

        btnEnable.setOnClickListener(listener);
        btnDisable.setOnClickListener(listener);
        }
//
//    private class MyAsyncTask extends AsyncTask<Void,Integer,Boolean> {
//        private int scannerId;
//        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
//        public MyAsyncTask(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode){
//            this.scannerId=scannerId;
//            this.opcode=opcode;
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//        @Override
//        protected Boolean doInBackground(String ... strings) {
//            if (sdkHandler != null)
//            {
//                String inXML = strings[0];
//                StringBuilder outXML=new StringBuilder();
//// calling execute command SDK API
//                DCSSDKDefs.DCSSDK_RESULT result =
//                        sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(
//                                opcode,inXML,outXML,scannerId);
//// return true if DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS
//// false otherwise
//                if(result== DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS) {
//                    return true;
//                }
//                else if(result==DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
//                    return false;
//            }
//             return false;
//        }

//        @Override
//        protected void onPostExecute(Boolean b) {
//            super.onPostExecute(b);
//        }
 //   }



    public void disableScanner(View view) {
        String in_xml = "<inArgs><scannerID>" + connectedScannerID + "</scannerID></inArgs>";
//        new MyAsyncTask (
//            connectedScannerID,DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_DISABLE).
//            execute(new String[]{in_xml});


        textView = findViewById(R.id.ScanData);
        textView.append(in_xml);
        textView.append("야호 테스트\n");
    }


    @Override
    public void dcssdkEventScannerAppeared(DCSScannerInfo dcsScannerInfo) {

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
        //Barcode barcode = new Barcode(barcodeData,barcodeType,fromScannerID);
        //dataHandler.obtainMessage(Constants.BARCODE_RECEIVED,barcode).sendToTarget();
        textView.append(code);
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