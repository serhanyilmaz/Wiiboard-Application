package com.tfkb.mobileapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tfkb.mobileapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DovizTakipAsyncTask extends AsyncTask<String, String, List<String>> {

	private static final String TAG = "DovizTakipAsyncTask";
	private Context context;
	private ListView dovizOranListView;
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter;
	private PendingIntent mContentIntent;
	public DovizTakipAsyncTask(Context context) {
		super();
		this.context = context;
		dovizOranListView = (ListView) ((Activity)context).findViewById(R.id.dovizOranListView);
	}

	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(context, "Lutfen Bekleyin...", "Islem Yurutuluyor...",true);
	}

	protected List<String> doInBackground(String... params) {

		return getDovizOranList(params[0]);

	}

	private List<String> getDovizOranList(String dovizTakipUrl) {

		HttpURLConnection urlConnection = null;

		try {

			publishProgress("HTTP baglantisi kuruluyor...");

			URL url = new URL(dovizTakipUrl);
			urlConnection = (HttpURLConnection) url.openConnection();

			int sonucKodu = urlConnection.getResponseCode();
			if (sonucKodu == HttpURLConnection.HTTP_OK) {
				BufferedInputStream stream = new BufferedInputStream(urlConnection.getInputStream());

				publishProgress("Doviz oranlari okunuyor...");

				List<String> dovizOranList = getDovizOranListFromInputStream(stream);

				publishProgress("Liste guncelleniyor...");
				return dovizOranList;
			}

		} catch (Exception e) {
			Log.d(TAG, "HTTP baglantisi kurulurken hata olustu", e);
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}

		return new ArrayList<String>();

	}

	private List<String> getDovizOranListFromInputStream(BufferedInputStream stream) {

		List<String> dovizOranList = new ArrayList<String>();

		if (stream == null)
			return dovizOranList;

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(stream);
			Element firstCube = (Element) document.getElementsByTagName("GetAllExchangeRateForCustomerXmlResponse").item(0);
			Element secondCube = (Element) firstCube.getElementsByTagName("GetAllExchangeRateForCustomerXmlResult").item(0);

			NodeList dovizOranNodeList = secondCube.getElementsByTagName("a:Rate");

			int dovizOranNodeListLength = dovizOranNodeList.getLength();


			for (int i = 0; i < dovizOranNodeListLength ; i++) {

				Element element= (Element) dovizOranNodeList.item(i); //üç farklı paket var


				NodeList nodeListAlis=element.getElementsByTagName("a:EffectiveBuying");
				NodeList nodeListBirim=element.getElementsByTagName("a:CurrencyCode");
				NodeList nodeListSatis=element.getElementsByTagName("a:EffectiveSelling");


				String alis=nodeListAlis.item(0).getFirstChild().getNodeValue();
				String birim=nodeListBirim.item(0).getFirstChild().getNodeValue();

				String satis=nodeListSatis.item(0).getFirstChild().getNodeValue();

				double buy=Double.parseDouble(alis);
				double sell=Double.parseDouble(satis);


					if(birim.equalsIgnoreCase("USD"))
					{dovizOranList.add( birim+"/Dolar ->"+"  Alış: "+String.format( "%.4f", buy )+"     Satış: "+String.format( " %.4f", sell ) );

					}
				if(birim.equalsIgnoreCase("EUR"))
				{dovizOranList.add( birim+" ->/Euro"+"  Alış: "+String.format( "%.4f", buy )+"     Satış: "+String.format( " %.4f", sell ) );

				}
				if(birim.equalsIgnoreCase("GBP"))
				{dovizOranList.add( birim+" ->/Sterlin"+"  Alış: "+String.format( "%.4f", buy )+"     Satış: "+String.format( " %.4f", sell ) );

				}
				if(birim.equalsIgnoreCase("JPY"))
				{dovizOranList.add( birim+" ->/Japon Yeni"+"  Alış: "+String.format( "%.4f", buy )+"     Satış: "+String.format( " %.4f", sell ) );

				}






			}

		} catch (Exception e) {
			Log.d(TAG, "XML parse edilirken hata olustu", e);
		}

		return dovizOranList;
	}

	protected void onProgressUpdate(String... progress) {
		progressDialog.setMessage(progress[0]);
	}

	protected void onPostExecute(List<String> result)
	{
		adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, result);
		dovizOranListView.setAdapter(adapter);
		progressDialog.cancel();
	}

}
