package com.mkahawa.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.nfc.NfcAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mkahawa.Helpers.UserPrefs;
import com.mkahawa.Listener;
import com.mkahawa.Helpers.QRScanner;
import com.mkahawa.R;

public class VerificationActivity extends AppCompatActivity implements Listener {

    private Button btnNext;
    private TextView tvMsg;
    public static boolean isNfcSupported = false, isNfcEnabled = false;
    private QRScanner qrScanner;
    public static boolean isDialogDisplayed = false;
    public static boolean isWrite = false;
    private UserPrefs userPrefs;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verification);

//        Toolbar toolbar = findViewById(R.id.toolbar_verif);
//        setSupportActionBar(toolbar);

        btnNext = findViewById(R.id.btn_init_next);
        tvMsg = findViewById(R.id.tv_init_msg);
        userPrefs = new UserPrefs(this);
        tvMsg.setText("To Get Started, Scan The QR Code On The Table");


        /*************************************************************/
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcAdapter = null;
        /**************************************************************/

        //phone supports nfc
        if(mNfcAdapter != null){
            isNfcSupported = true;
            try {
                mNfcAdapter.isEnabled();
            } catch (Exception e) {}
            try {
                isNfcEnabled = mNfcAdapter.isEnabled();
                if(isNfcEnabled){
//                    Toast.makeText(this, "NFC enabled", Toast.LENGTH_SHORT).show();
                    tvMsg.setText("To get started, Press Next and tap your phone on the card on the table");
                }
                else{
//                    Toast.makeText(this, "NFC not enabled", Toast.LENGTH_SHORT).show();
                    tvMsg.setText("To get started, Press Next to enable NFC");
                }

            } catch (Exception e) {}
        }
        else{
            isNfcSupported = false;
//            Toast.makeText(this, "NFC not supported", Toast.LENGTH_SHORT).show();
            tvMsg.setText("To Get Started, Scan The QR Code On The Table");
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String signedInStatus = userPrefs.getSignedInStatus();
                if(signedInStatus.equals("false")){
                    // user signed out
                    Intent i = new Intent(VerificationActivity.this, SignInActivity.class);
                    i.putExtra("SignedIn", "false");
                    startActivity(i);
                }
                if(signedInStatus.equals("true")) {
                    if (isNfcSupported) {
//                        nfcInstance = new NFC(thisActivity, mNfcAdapter);
//                        if (isNfcEnabled) {
//                            // NFC functionality is available and enabled
//                            nfcInstance.initNFC();
//                        } else {
//                            //ask user to enable nfc
//                            nfcInstance.enableNFC();
//                        }
                    } else {
                        // TODO: 12/10/2018 Clean this up so that you have all views in scannerActivity
                        startActivity(new Intent(VerificationActivity.this, ScannerActivity.class));
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(qrScanner != null){
            qrScanner.startScanner();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(qrScanner != null){
            qrScanner.closeScanner();
        }
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite = false;
    }
}
