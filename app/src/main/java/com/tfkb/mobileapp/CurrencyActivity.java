package com.tfkb.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;


public class CurrencyActivity extends Activity {

    private DovizTakipAsyncTask task;
    int sira_alim_sayisi=0;
    WifiManager wifiManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent serviceintent=new Intent(CurrencyActivity.this,WifiService.class);
        startService(serviceintent);
        startService(new Intent(CurrencyActivity.this,SurveyService.class));
        task = new DovizTakipAsyncTask(this);
        task.execute(getResources().getString(R.string.currency_url));

        Button yenileButton = (Button) findViewById(R.id.yenileButton);
        Button routeButton = (Button) findViewById(R.id.btnRouteBackupAccount);
        final Button orderbutton = (Button) findViewById(R.id.btnTakeOrder);
        yenileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                task = new DovizTakipAsyncTask(CurrencyActivity.this);
                task.execute(getResources().getString(R.string.currency_url));

            }
        });

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ıntent=new Intent(CurrencyActivity.this,TFKBBackupAccount.class);
                startActivity(ıntent);
            }
        });

        orderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               alert();
            }
        });

    }
    public void alert(){
        String aylar[]={"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
        Calendar simdi= Calendar.getInstance();

        Random r=new Random();

        int generate_number=r.nextInt(1000);
        int ay= 1+ simdi.get(Calendar.MONTH);

        sira_alim_sayisi =sira_alim_sayisi +1;

        int [] order_number=new int[1000];
        String[] order_date=new String[1000];
        String generate_date=simdi.get(Calendar.DATE)+"/"+ay+"/"+simdi.get(Calendar.YEAR)+" "+simdi.get(Calendar.HOUR_OF_DAY)+
                ":"+simdi.get(Calendar.MINUTE)+":"+simdi.get(Calendar.SECOND);

       for(int k=0;k<=sira_alim_sayisi;k++)
       {
           // TODO Auto-generated method stub
           AlertDialog.Builder alertadd = new AlertDialog.Builder(
                   CurrencyActivity.this);
           alertadd.setTitle("TÜRKİYE FİNANS KATILIM BANKASI");

           LayoutInflater factory = LayoutInflater.from(CurrencyActivity.this);
           final View view = factory.inflate(R.layout.alert_dialog, null);

           ImageView image= (ImageView) view.findViewById(R.id.imgTFKB);
           image.setImageResource(R.drawable.tfkb);

           TextView text= (TextView) view.findViewById(R.id.txtSira);
           text.setText(generate_date +" TARİHİNDE ALDIĞINIZ SIRA NO: TFKB"+generate_number);

           alertadd.setView(view);
           alertadd.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dlg, int sumthin) {
                   Toast.makeText(getApplicationContext(),"SIRA ALMA İŞLEMİNİZ BAŞARI İLE GERÇEKLEŞTİ..", Toast.LENGTH_SHORT).show();
               }
           });

           alertadd.show();
           order_date[k]=generate_date;
           order_number[k]=generate_number;

           break;


       }


        if(sira_alim_sayisi >2)
        {
            AlertDialog.Builder alertadd1 = new AlertDialog.Builder(
                    CurrencyActivity.this);
            alertadd1.setTitle("TÜRKİYE FİNANS KATILIM BANKASI");

            LayoutInflater factory = LayoutInflater.from(CurrencyActivity.this);
            final View view = factory.inflate(R.layout.alert_dialog, null);

            ImageView image= (ImageView) view.findViewById(R.id.imgTFKB);
            image.setImageResource(R.drawable.tfkb);

            TextView text= (TextView) view.findViewById(R.id.txtSira);
            text.setText("AYNI GÜN İÇİNDE ALINAN SIRA ALMA HAKKINIZI DOLDURDUNUZ.BİR SONRAKİ DENEMENİZİ 1 SAAT SONRA TEKRAR DENEYİNİZ");

            alertadd1.setView(view);
            alertadd1.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dlg, int sumthin) {
                    Toast.makeText(getApplicationContext(),"İŞLEMİNİZ SONA ERMİŞTİR..", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(CurrencyActivity.this,CurrencyActivity.class);
                    startActivity(i);
                }


            });

            alertadd1.show();



        }

    }

    @Override
    protected void onResume() {
       Intent serviceintent=new Intent(CurrencyActivity.this,WifiService.class);
        startService(serviceintent);
        startService(new Intent(CurrencyActivity.this,SurveyService.class));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Intent serviceintent=new Intent(CurrencyActivity.this,WifiService.class);
        startService(serviceintent);
        startService(new Intent(CurrencyActivity.this,SurveyService.class));
        super.onDestroy();
    }
}
