package com.tfkb.mobileapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class MainActivity extends Activity {

    /**
     * Mobile Service Client reference
     */
    private  View view;
    private MobileServiceClient mClient;
    private ProgressBar progressBar;
    WifiManager wifiManager;
    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<ToDoItem> mTodoTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<ToDoItem> mTodoTable;

    /**
     * Adapter to sync the items list with the view
     */
    private ToDoItemAdapter mAdapter;


    /**
     * EditText containing the "New To Do" text
     */
    private Button mTextNewToDoSmile,mTextNewToDoAvarage,mTextNewToDoSad;
    String survey=null;
    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    /**
     * Initializes the activity
     */

    double cok_iyi_oran=0,orta_oran=0,cok_kotu_oran=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_main);
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://wiiresource.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mTodoTable = mClient.getTable(ToDoItem.class);

            // Offline Sync
            //mTodoTable = mClient.getSyncTable("ToDoItem", ToDoItem.class);

            //Init local storage
            initLocalStore().get();

            mTextNewToDoSmile = (Button) findViewById(R.id.btnSmile);
            mTextNewToDoAvarage = (Button) findViewById(R.id.btnAvarage);
            mTextNewToDoSad = (Button) findViewById(R.id.btnSad);


            // Create an adapter to bind the items with the view
            mAdapter = new ToDoItemAdapter(this, R.layout.row_list_to_do);
            final ListView   listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);
            mTextNewToDoSmile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    survey="ÇOK İYİ";
                    addItem(view);

                    int c=1,o=0,ck=0;
                    for(int i=0 ;i< listViewToDo.getAdapter().getCount();i++)
                    {
                        if(listViewToDo.getAdapter().getItem(i).toString().equals("ÇOK İYİ"))
                        {
                            c++ ;

                        }
                        else  if(listViewToDo.getAdapter().getItem(i).toString().equals("ORTA"))
                        {
                            o++ ;

                        }
                        else if(listViewToDo.getAdapter().getItem(i).toString().equals("ÇOK KÖTÜ"))
                        {
                            ck++ ;

                        }


                    }


                    cok_iyi_oran=(double) (100*c)/(listViewToDo.getAdapter().getCount()+1);
                    orta_oran= (double) (100*o)/(listViewToDo.getAdapter().getCount()+1);
                    cok_kotu_oran= (double) (100*ck)/(listViewToDo.getAdapter().getCount()+1);

                    //    listViewToDo.getAdapter().getItem(4).toString() o sırada bulunan liste elemanını verir.
                    String aylar[]={"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
                    Calendar simdi=Calendar.getInstance();

                    Random r=new Random();


                    int ay= 1+ simdi.get(Calendar.MONTH);



                    final String generate_date=simdi.get(Calendar.DATE)+"/"+ay+"/"+simdi.get(Calendar.YEAR)+" "+simdi.get(Calendar.HOUR_OF_DAY)+
                            ":"+simdi.get(Calendar.MINUTE)+":"+simdi.get(Calendar.SECOND);


                    // TODO Auto-generated method stub
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(
                            MainActivity.this);
                    alertadd.setTitle("ANKET SONUÇLARI");

                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    v = factory.inflate(R.layout.alert_dialog, null);

                    ImageView image= (ImageView) v.findViewById(R.id.imgTFKB);
                    image.setImageResource(R.drawable.smile_face);

                    TextView text= (TextView) v.findViewById(R.id.txtSira);
            /*        text.setText(generate_date+"\n"+"\n"+"KULLANILAN OY SAYISI:"+(listViewToDo.getAdapter().getCount()+1)+"\n"+"\n"+
                            "ÇOK İYİ : %"+String.format( " %.2f", cok_iyi_oran )
                            +"\n"+"\n"+"ORTA : %"+String.format( " %.2f", orta_oran )
                            +"\n"+"\n"+"ÇOK KÖTÜ : %"+String.format( " %.2f", cok_kotu_oran ));   */

                    text.setText(generate_date +"\n"+" UYGULAMA HAKKINDAKİ GÖRÜŞÜNÜZ İÇİN TEŞEKKÜR EDERİZ");
                    alertadd.setView(v);
                    alertadd.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            //  finish();
                            //  System.exit(0);
                        }
                    });

                    alertadd.show();


                    mTextNewToDoSmile.setClickable(false);
                    mTextNewToDoAvarage.setClickable(false);
                    mTextNewToDoSad.setClickable(false);

                }
            });
            mTextNewToDoAvarage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    survey="ORTA";
                    addItem(view);
                    int c=0,o=1,ck=0;
                    for(int i=0 ;i< listViewToDo.getAdapter().getCount();i++)
                    {
                        if(listViewToDo.getAdapter().getItem(i).toString().equals("ÇOK İYİ"))
                        {
                            c++ ;

                        }
                        else  if(listViewToDo.getAdapter().getItem(i).toString().equals("ORTA"))
                        {
                            o++ ;

                        }
                        else if(listViewToDo.getAdapter().getItem(i).toString().equals("ÇOK KÖTÜ"))
                        {
                            ck++ ;

                        }


                    }


                    cok_iyi_oran=(double) (100*c)/(listViewToDo.getAdapter().getCount()+1);
                    orta_oran= (double) (100*o)/(listViewToDo.getAdapter().getCount()+1);
                    cok_kotu_oran= (double) (100*ck)/(listViewToDo.getAdapter().getCount()+1);

                    String aylar[]={"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
                    Calendar simdi=Calendar.getInstance();

                    Random r=new Random();


                    int ay= 1+ simdi.get(Calendar.MONTH);



                    final String generate_date=simdi.get(Calendar.DATE)+"/"+ay+"/"+simdi.get(Calendar.YEAR)+" "+simdi.get(Calendar.HOUR_OF_DAY)+
                            ":"+simdi.get(Calendar.MINUTE)+":"+simdi.get(Calendar.SECOND);


                    // TODO Auto-generated method stub
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(
                            MainActivity.this);
                    alertadd.setTitle("ANKET SONUÇLARI");

                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    v = factory.inflate(R.layout.alert_dialog, null);

                    ImageView image= (ImageView) v.findViewById(R.id.imgTFKB);
                    image.setImageResource(R.drawable.avarage_face);

                    TextView text= (TextView) v.findViewById(R.id.txtSira);
               /*     text.setText(generate_date+"\n"+"\n"+"KULLANILAN OY SAYISI:"+(listViewToDo.getAdapter().getCount()+1)+"\n"+"\n"+
                            "ÇOK İYİ : %"+String.format( " %.2f", cok_iyi_oran )
                            +"\n"+"\n"+"ORTA : %"+String.format( " %.2f", orta_oran )
                            +"\n"+"\n"+"ÇOK KÖTÜ : %"+String.format( " %.2f", cok_kotu_oran ));  */
                    text.setText(generate_date +"\n"+" ÖNERİNİZİ DİKKATE ALACAĞIZ...");
                    alertadd.setView(v);
                    alertadd.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {

                            //  finish();
                            //   System.exit(0);
                        }
                    });

                    alertadd.show();

                    mTextNewToDoSmile.setClickable(false);
                    mTextNewToDoAvarage.setClickable(false);
                    mTextNewToDoSad.setClickable(false);

                }
            });
            mTextNewToDoSad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    survey="ÇOK KÖTÜ";
                    addItem(view);

                    int c=0,o=0,ck=1;
                    for(int i=0 ;i< listViewToDo.getAdapter().getCount();i++)
                    {
                        if(listViewToDo.getAdapter().getItem(i).toString().equals("ÇOK İYİ"))
                        {
                            c++ ;

                        }
                        else  if(listViewToDo.getAdapter().getItem(i).toString().equals("ORTA"))
                        {
                            o++ ;

                        }
                        else if(listViewToDo.getAdapter().getItem(i).toString().equals("ÇOK KÖTÜ"))
                        {
                            ck++ ;

                        }


                    }


                    cok_iyi_oran=(double) (100*c)/(listViewToDo.getAdapter().getCount()+1);
                    orta_oran= (double) (100*o)/(listViewToDo.getAdapter().getCount()+1);
                    cok_kotu_oran= (double) (100*ck)/(listViewToDo.getAdapter().getCount()+1);

                    String aylar[]={"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
                    Calendar simdi=Calendar.getInstance();

                    Random r=new Random();


                    int ay= 1+ simdi.get(Calendar.MONTH);



                    final String generate_date=simdi.get(Calendar.DATE)+"/"+ay+"/"+simdi.get(Calendar.YEAR)+" "+simdi.get(Calendar.HOUR_OF_DAY)+
                            ":"+simdi.get(Calendar.MINUTE)+":"+simdi.get(Calendar.SECOND);


                    // TODO Auto-generated method stub
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(
                            MainActivity.this);
                    alertadd.setTitle("ANKET SONUÇLARI");

                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    v = factory.inflate(R.layout.alert_dialog, null);

                    ImageView image= (ImageView) v.findViewById(R.id.imgTFKB);
                    image.setImageResource(R.drawable.sad_face);

                    TextView text= (TextView) v.findViewById(R.id.txtSira);
                /*    text.setText(generate_date+"\n"+"\n"+"KULLANILAN OY SAYISI:"+(listViewToDo.getAdapter().getCount()+1)+"\n"+"\n"+
                            "ÇOK İYİ : %"+String.format( " %.2f", cok_iyi_oran )
                            +"\n"+"\n"+"ORTA : %"+String.format( " %.2f", orta_oran )
                            +"\n"+"\n"+"ÇOK KÖTÜ : %"+String.format( " %.2f", cok_kotu_oran )); */
                    text.setText(generate_date +"\n"+" SİZİN İÇİN GEREKLİ DÜZENLEMELERİ EN KISA ZAMANDA YAPACAĞIZ ");

                    alertadd.setView(v);
                    alertadd.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            //   finish();
                            //   System.exit(0);
                        }
                    });

                    alertadd.show();

                    mTextNewToDoSmile.setClickable(false);
                    mTextNewToDoAvarage.setClickable(false);
                    mTextNewToDoSad.setClickable(false);

                }
            });




            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }



    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Select an option from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refreshItemsFromTable();
        }

        return true;
    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkItem(final ToDoItem item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void checkItemInTable(ToDoItem item) throws ExecutionException, InterruptedException {
        mTodoTable.update(item).get();
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addItem(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final ToDoItem item = new ToDoItem();

        item.setText(survey.toString());
        item.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final ToDoItem entity = addItemInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mAdapter.add(entity);


                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);


    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public ToDoItem addItemInTable(ToDoItem item) throws ExecutionException, InterruptedException {
        ToDoItem entity = mTodoTable.insert(item).get();
        return entity;
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<ToDoItem> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (ToDoItem item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<ToDoItem> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mTodoTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ToDoItem> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mTodoTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("text", ColumnDataType.String);
                    tableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("ToDoItem", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mTodoTableSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mTodoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}