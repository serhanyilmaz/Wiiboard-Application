package com.tfkb.mobileapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Serhan on 16.8.2016.
 */
public class TFKBBackupAccount extends Activity {
    Button buttonGeri, buttonIleri, buttonAnasayfa, buttonGO;
    WebView webViewFK;
    EditText editTextURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tfkb_backup_account_web);
        webViewFK = (WebView) findViewById(R.id.webFK);
        webViewFK.getSettings().setJavaScriptEnabled(true);
        webViewFK.setWebViewClient(new MyWebViewClient());
        webViewFK.loadUrl("http://www.turkiyefinans.com.tr/tr-tr/bireysel/sayfalar/yedek-hesap.aspx");

        buttonGeri = (Button) findViewById(R.id.buttonGeri);
        buttonIleri = (Button) findViewById(R.id.buttonIleri);
        buttonAnasayfa = (Button) findViewById(R.id.buttonAnasayfa);

        buttonAnasayfa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (InternetKontrol())
                    webViewFK.loadUrl("http://www.turkiyefinans.com.tr/tr-tr/Sayfalar/default.aspx");
                else
                    BaglantiHatasiVer();
            }
        });

        buttonGeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (webViewFK.canGoBack()) {
                    webViewFK.goBack();
                }
            }
        });

        buttonIleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (webViewFK.canGoForward()) {
                    webViewFK.goForward();
                }
            }
        });
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    public void BaglantiHatasiVer() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TFKBBackupAccount.this);
        alertDialogBuilder.setTitle("Sunucu Hatası");
        alertDialogBuilder.setMessage(
                "internet bağlantınızı kontrol edip tekrar deneyin")
                .setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean InternetKontrol() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null
                && manager.getActiveNetworkInfo().isAvailable()
                && manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
