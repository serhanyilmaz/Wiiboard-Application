package com.tfkb.mobileapp;

/**
 * Created by Serhan on 20.9.2016.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;


import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

public class SelectWifiActicity extends Activity  {

    TextView tekdeger;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
    int degerbul = -1;
    Spinner spinner;
    TextView textView2;
    String secilenag;
    Button approve, select,process,survey;
    SharedPreferences sharedPref=null;
    String savedString=null;
    List<String> listOfProvider;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_wifi_layout);
        Intent serviceintent=new Intent(SelectWifiActicity.this,WifiService.class);
        startService(serviceintent);
        startService(new Intent(SelectWifiActicity.this,SurveyService.class));
        tekdeger = (TextView) findViewById(R.id.textview);
        spinner = (Spinner) findViewById(R.id.spinner);
        approve = (Button) findViewById(R.id.btnApprove);
        select = (Button) findViewById(R.id.btnSaveWifi);
        process = (Button) findViewById(R.id.btnProcessGo);
        survey = (Button) findViewById(R.id.btnSurveyGo);

        // Initializing an ArrayAdapter
        textView2 = (TextView) findViewById(R.id.textView2);
         sharedPref=getPreferences(Context.MODE_PRIVATE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String[] dersDizisi = getResources().getStringArray(R.array.dersler);
                textView2.setText(wifis[position] + " ağını seçtiniz!");
                secilenag = wifis[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", "istenenWifiAdi");
        wifiConfig.preSharedKey = String.format("\"%s\"", "password");
        //WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
      //  int netId = wifi.addNetwork(wifiConfig);
      //  wifi.disconnect();
       // wifi.enableNetwork(netId, true);
     //   wifi.reconnect();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

            //Butonlara tıklanınca
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sharedPref = getPreferences(Context.MODE_PRIVATE);
                String stringValue = secilenag; //Edittextten alınıyor


                if(spinner == null || spinner.getSelectedItem() ==null){ //alınan değerlerin boş olup olmaması kontrol ediliyor

                    Toast.makeText(getApplicationContext(), "Lütfen Bir Wifi Seçiniz.",Toast.LENGTH_LONG).show();

                }else{ //değerler boş değilse


                    SharedPreferences.Editor editor = sharedPref.edit(); //SharedPreferences'a kayıt eklemek için editor oluşturuyoruz

                    editor.putString("stringValue",stringValue); //string değer ekleniyor

                    editor.commit(); //Kayıt
                    Toast.makeText(getApplicationContext(), "Kayıt Yapıldı.",Toast.LENGTH_LONG).show();

                }
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kayıtlı verileri getir butonu

                //kaydedilen veriler kaydedilen key ile geri çekiliyor.
                //Eğer o key ile eşlesen bir değer yoksa default  value cekilir
                //örneğin "stringValue" değeri ile bir kayıt yoksa savedString'e değer olarak "Kayıt yok" atanacak
             savedString = sharedPref.getString("stringValue","Kayıt Yok");

                AlertDialog.Builder builder = new AlertDialog.Builder(SelectWifiActicity.this);
                builder.setTitle("TFKB MOBIL APP");
                if(savedString.equals("Kayıt Yok")){
                    builder.setMessage("Önce Kayıt Yapınız");
                }else{ //Kayıtlı değerler yazdırılıyor
                    builder.setMessage("Kayıtlı SSID : "+savedString);
                }

                builder.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.show();
            }
        });

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SelectWifiActicity.this,CurrencyActivity.class);
                startActivity(i);
            }
        });
        survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SelectWifiActicity.this,MainActivity.class);
                startActivity(i);
            }
        });


            //myString.substring(myString.indexOf("dunya"), myString.length())
            //

        }






    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.

    }

    @Override
    public void onStop() {
        super.onStop();


    }




    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];
            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = ((wifiScanList.get(i)).toString());
                if (wifis[i].contains("mobx")) {
                    degerbul = i;
                }
                if (wifis[i].contains("SSID:")) {
                    //wifis[i]=wifis[i].substring(wifis[i].indexOf("SSID:"), wifis[i].indexOf("BSSID:")-1);
                    wifis[i] = wifis[i].substring(5, wifis[i].indexOf("BSSID:") - 2);
                }

                //myString.substring(myString.indexOf("dunya"), myString.length())
                //

            }


            spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, wifis));
            if (degerbul != -1) {
                //tekdeger.setText(wifis[degerbul]);
                tekdeger.setText("Seçtiğiniz Wifi Adı");
                /*WifiConfiguration wifiConfig =new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", "istenenWifiAdi");
                wifiConfig.preSharedKey = String.format("\"%s\"", "wifiPasword");
                //WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                int netId = wifi.addNetwork(wifiConfig);
                wifi.disconnect();
                wifi.enableNetwork(netId, true);
                wifi.reconnect();
                //programsonlanınca bağlanıyor
                */

            } else {
                tekdeger.setText("Kablosuz ağların listesinde aranılan ağ bulunamadı");
            }


        }
    }
}
