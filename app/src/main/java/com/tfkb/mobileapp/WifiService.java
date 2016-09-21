package com.tfkb.mobileapp;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WifiService extends Service {
    Context context ;
    Notification notification;
    Timer timer;
    WifiManager wifiManager;
    String cevap;
    List<String> listOfProvider;
    List<ScanResult> wifiList;
    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE_BROADCAST_KEY="StopServiceBroadcastKey";
    final static int RQS_STOP_SERVICE = 1;
    NotifyServiceReceiver notifyServiceReceiver;
    Thread thread;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

        return null;
    }
    @Override
    public void onCreate() {//Servis startService(); metoduyla çağrıldığında çalışır
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        notifyServiceReceiver = new NotifyServiceReceiver();
        context = getApplicationContext();
        Toast.makeText(this, "Servis Çalıştı.Bu Mesaj Servis Class'dan", Toast.LENGTH_SHORT).show();


        timer = new Timer();
        timer.schedule(new TimerTask() {  // Günde bir kere bildirimGonder(); metodu çağırılır.
            @Override
            public void run() {
                bildirimGonder();
            }

        }, 0, AlarmManager.INTERVAL_HALF_DAY);



    }
    public void bildirimGonder(){// Burda servis class dan post edip sunucudan aldığımız değeri bildirim gönderiyoruz.
        listOfProvider = new ArrayList<String>();
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiList = wifiManager.getScanResults();
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
                    ;
            listOfProvider.add(providerName);
            if (wifiList.get(i).SSID.equalsIgnoreCase(getResources().getString(R.string.wifi_name))  ) {


                thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);

                                //    PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), transectionpage, 0);
                                Notification myNotify = null;
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
                        } catch (InterruptedException ex) {
                        }

                        // TODO
                    }
                };

                thread.start();
                break;

            }

        else    if(i == wifiList.size()-1 && !wifiList.get(i).SSID.equalsIgnoreCase(getResources().getString(R.string.wifi_name))  )
            {









                thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);

                                // PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), transectionpage, 0);
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
                        } catch (InterruptedException ex) {
                        }

                        // TODO
                    }
                };

                thread.start();
                break;
            }


        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {//Servis stopService(); metoduyla durdurulduğunda çalışır
        //timer.cancel();
        //Toast.makeText(this, "Servis Durduruldu.Bu Mesaj Servis Class'dan", Toast.LENGTH_SHORT).show();
       // this.unregisterReceiver(notifyServiceReceiver);


    }
    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            int rqs = arg1.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);

            if (rqs == RQS_STOP_SERVICE){
                stopSelf();
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancelAll();
            }
            Intent serviceIntent = new Intent(context, WifiService.class);
            arg0.startService(serviceIntent);
        }
    }

}
