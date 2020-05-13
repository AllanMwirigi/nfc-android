package com.mkahawa;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mkahawa.Activities.VerificationActivity;

/**
 * Created by Allan Mwirigi on 9/20/2018.
 */

public class NFC extends Activity {
    private Activity context;
    private EditText mEtMessage;
    private Button mBtWrite, mBtRead;
    private TextView tvMsg;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;
    private NfcAdapter mNfcAdapter;

    public static final String TAG = MainActivity.class.getSimpleName();

    public NFC(Activity context, NfcAdapter mNfcAdapter){
        this.context = context;
        this.mNfcAdapter = mNfcAdapter;
        tvMsg = context.findViewById(R.id.tv_message);
    }

    public void initNFC(){
        context.setContentView(R.layout.nfc);
        mEtMessage = context.findViewById(R.id.et_message);
        mBtWrite = context.findViewById(R.id.btn_write);
        mBtRead = context.findViewById(R.id.btn_read);

        mBtWrite.setOnClickListener(view -> showWriteFragment());
        mBtRead.setOnClickListener(view -> showReadFragment());
//        Toast.makeText(context, "Skipping NFC for now", Toast.LENGTH_SHORT).show();
//        context.startActivity(new Intent(context, MainActivity.class));
    }

    public void enableNFC(){
        if (Build.VERSION.SDK_INT >= 16) {
//            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            context.startActivityForResult(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS), 345);
        } else {
//            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            context.startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 355);
        }
    }

    public void startNFC(){
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(context, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("NFC", "Activity Result");
        if(requestCode == 345 || requestCode == 355){
            try {
                mNfcAdapter.isEnabled();
            } catch (Exception e) {}
            try {
                VerificationActivity.isNfcEnabled = mNfcAdapter.isEnabled();
                if(VerificationActivity.isNfcEnabled){
                    Toast.makeText(context, "NFC enabled", Toast.LENGTH_SHORT).show();
                    tvMsg.setText("Press Next and tap your phone on the card on the table");
                }

                else{
                    Toast.makeText(context, "NFC not enabled", Toast.LENGTH_SHORT).show();
                    tvMsg.setText("To get started, Press Next to enable NFC");
                }


            } catch (Exception e) {}
        }
        else{
            Toast.makeText(context, "wrong request code", Toast.LENGTH_SHORT).show();
        }
    }

    private void showWriteFragment() {

        VerificationActivity.isWrite = true;

        mNfcWriteFragment = (NFCWriteFragment) context.getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);

        if (mNfcWriteFragment == null) {

            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(context.getFragmentManager(),NFCWriteFragment.TAG);

    }

    private void showReadFragment() {

        mNfcReadFragment = (NFCReadFragment) context.getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(context.getFragmentManager(),NFCReadFragment.TAG);

    }



    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null) {
            Toast.makeText(context, context.getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (VerificationActivity.isDialogDisplayed) {
                if (VerificationActivity.isWrite) {
                    String messageToWrite = mEtMessage.getText().toString();
                    mNfcWriteFragment = (NFCWriteFragment) context.getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite);

                } else {

                    mNfcReadFragment = (NFCReadFragment)context.getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    mNfcReadFragment.onNfcDetected(ndef);
                }
            }
        }
    }

    public void stopNFC(){
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(context);
    }

//    private AlertDialog initDialog(String title, String msg){
//        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//        alertBuilder.setTitle("title").setMessage(msg).setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .setNega
//        return null;
//    }
}
