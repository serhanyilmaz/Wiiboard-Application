package com.tfkb.mobileapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tfkb.mobileapp.adapter.ListAdapter;
import com.tfkb.mobileapp.javafiles.ImportDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WifiActivity extends Activity implements OnClickListener {
    Button setWifi;
    WifiManager wifiManager;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    List<String> listOfProvider;
    ListAdapter adapter;
    ListView listViwProvider;
   String[]list=new String[1000];
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_list);
            Intent serviceintent=new Intent(WifiActivity.this,WifiService.class);
        startService(serviceintent);
        startService(new Intent(WifiActivity.this,SurveyService.class));
        listOfProvider = new ArrayList<String>();

		/*setting the resources in class*/
        listViwProvider = (ListView) findViewById(R.id.list_view_wifi);
        setWifi = (Button) findViewById(R.id.btn_wifi);

        setWifi.setOnClickListener(this);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        /*checking wifi connection
         * if wifi is on searching available wifi provider*/
        if (wifiManager.isWifiEnabled() == true) {
            setWifi.setText("ON");
            scaning();
        }
		/*opening a detail dialog of provider on click   */
        listViwProvider.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ImportDialog action = new ImportDialog(WifiActivity.this, (wifiList.get(position)).toString());
                action.showDialog();
            }
        });

    }



    /*setting the functionality of ON/OFF button*/
    @Override
    public void onClick(View arg0) {
		/* if wifi is ON set it OFF and set button text "OFF" */
        if (wifiManager.isWifiEnabled() == true) {
            wifiManager.setWifiEnabled(false);
            setWifi.setText("OFF");
            listViwProvider.setVisibility(ListView.GONE);
            scaning();

        }
		/* if wifi is OFF set it ON 
		 * set button text "ON" 
		 * and scan available wifi provider*/
        else if (wifiManager.isWifiEnabled() == false) {
            wifiManager.setWifiEnabled(true);
            setWifi.setText("ON");
            listViwProvider.setVisibility(ListView.VISIBLE);

        }
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);



    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        super.onResume();

    }

    @Override
    protected void onDestroy() {
       Intent serviceintent=new Intent(WifiActivity.this,WifiService.class);
       startService(serviceintent);
        startService(new Intent(WifiActivity.this,SurveyService.class));
        super.onDestroy();

    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            wifiList = wifiManager.getScanResults();

			/* sorting of wifi provider based on level */
            Collections.sort(wifiList, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level > rhs.level ? -1
                            : (lhs.level == rhs.level ? 0 : 1));
                }
            });
            listOfProvider.clear();
            String providerName;
            for (int i = 0; i < wifiList.size(); i++) {
				/* to get SSID and BSSID of wifi provider*/

                providerName = (wifiList.get(i).SSID).toString()
                        + "\n" + (wifiList.get(i).BSSID).toString()
                +"\n"+calculateDistance((double)wifiList.get(i).level, wifiList.get(i).frequency);
                listOfProvider.add(providerName);
                if (wifiList.get(i).SSID.equalsIgnoreCase(getResources().getString(R.string.wifi_name))  ) {
                    final WifiReceiver myActivity = this;

                   thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(1000);
                                    Intent transectionpage = new Intent(WifiActivity.this, CurrencyActivity.class);
                                    startActivity(transectionpage);
                                //    PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), transectionpage, 0);

                                 /*   timer = new Timer();
                                    timer.schedule(new TimerTask() {  // Günde bir kere bildirimGonder(); metodu çağırılır.
                                        @Override
                                        public void run() {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                                String cevap = "Türkiye Finans Katılım Bankasına Hoşgeldiniz...";

                                                int icon = R.drawable.tfkb;//notificationda gösterilecek icon
                                                //  long when = System.currentTimeMillis();//notificationın ne zaman gösterileceği
                                                long when = System.currentTimeMillis();//notificationın ne zaman gösterileceği
                                                String baslik = "TFKB HOŞGELDİN MESAJI";//notification başlık
                                                NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                                Intent intent=new Intent(getApplicationContext(),CurrencyActivity.class);
                                                PendingIntent pending= PendingIntent.getActivity(getApplicationContext(), 0, intent,0);//Notificationa tıklanınca açılacak activityi belirliyoruz
                                                Notification notification;
                                                notification = new Notification(icon, " TFKB HOŞGELDİN MESAJI", when);
                                                notification.setLatestEventInfo(getApplicationContext(),baslik,cevap,pending);

                                                notification.flags |= Notification.FLAG_AUTO_CANCEL;//notificationa tıklanınca notificationın otomatik silinmesi için
                                                notification.defaults |= Notification.DEFAULT_SOUND;//notification geldiğinde bildirim sesi çalması için
                                                notification.defaults |= Notification.DEFAULT_VIBRATE;//notification geldiğinde bildirim titremesi için
                                                nm.notify(0, notification);
                                            }

                                        }

                                    }, 0, AlarmManager.INTERVAL_HALF_DAY); */



                                }
                            } catch (InterruptedException ex) {
                            }

                            // TODO
                        }
                    };

                    thread.start();
                  break;

                }


             else if(i == wifiList.size()-1 && !wifiList.get(i).SSID.equalsIgnoreCase(getResources().getString(R.string.wifi_name))  )
             {
                 final WifiReceiver myActivity = this;

                 thread = new Thread() {
                     @Override
                     public void run() {
                         try {
                             synchronized (this) {
                                 wait(1000);
                                 Intent transectionpage = new Intent(WifiActivity.this, MainActivity.class);
                                 startActivity(transectionpage);
                                // PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), transectionpage, 0);
                               /* timer = new Timer();
                                 timer.schedule(new TimerTask() {  // Günde bir kere bildirimGonder(); metodu çağırılır.
                                     @Override
                                     public void run() {
                                         Notification myNotify = null;
                                         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                             String cevap = "Ankete Katılmak İçin Lütfen Bildirime Tıklayınız.";

                                             int icon = R.drawable.tfkb;//notificationda gösterilecek icon
                                             //  long when = System.currentTimeMillis();//notificationın ne zaman gösterileceği
                                             long when = System.currentTimeMillis();//notificationın ne zaman gösterileceği
                                             String baslik = "TFKB ANKET";//notification başlık
                                             NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                             Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                             PendingIntent pending= PendingIntent.getActivity(getApplicationContext(), 0, intent,0);//Notificationa tıklanınca açılacak activityi belirliyoruz
                                             Notification notification;
                                             notification = new Notification(icon, " TFKB ANKET", when);
                                             notification.setLatestEventInfo(getApplicationContext(),baslik,cevap,pending);
                                             notification.flags |= Notification.FLAG_AUTO_CANCEL;//notificationa tıklanınca notificationın otomatik silinmesi için
                                             notification.defaults |= Notification.DEFAULT_SOUND;//notification geldiğinde bildirim sesi çalması için
                                             notification.defaults |= Notification.DEFAULT_VIBRATE;//notification geldiğinde bildirim titremesi için
                                             nm.notify(0, notification);

                                         }
                                     }

                                 }, 0, AlarmManager.INTERVAL_HALF_DAY);

                               */

                             }
                         } catch (InterruptedException ex) {
                         }

                         // TODO
                     }
                 };

                 thread.start();
                 break;
             }


            }



			/*setting list of all wifi provider in a List*/
            adapter = new ListAdapter(WifiActivity.this, listOfProvider);
            listViwProvider.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }
    }
    public double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private void scaning() {
        // wifi scaned value broadcast receiver
        receiverWifi = new WifiReceiver();
        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi
        // connections changed
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();

    }


}
