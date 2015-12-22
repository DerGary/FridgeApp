package com.example.student.gefriertruhapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.student.gefriertruhapp.DetailFragments.FridgeDetailFragment;
import com.example.student.gefriertruhapp.DetailFragments.ShelfDetailFragment;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.Notifications.BackgroundNotificationService;
import com.example.student.gefriertruhapp.ViewPager.ViewPagerFragment;

import java.sql.DatabaseMetaData;

public class Dashboard extends ActionBarActivity {
    //Actions
    public static final String SCAN = "la.droid.qr.scan";
    public static final String ENCODE = "la.droid.qr.encode";
    public static final String DECODE = "la.droid.qr.decode";

    //Parameters
    //SCAN / DECODE
    public static final String COMPLETE = "la.droid.qr.complete"; //Default: false
    //ENCODE
    public static final String CODE =  "la.droid.qr.code"; //Required
    public static final String SIZE = "la.droid.qr.size"; //Default: Fit screen
    //ENCODE / DECODE
    public static final String IMAGE =  "la.droid.qr.image"; //Default for encode: false / Required for decode

    //Result
    public static final String RESULT = "la.droid.qr.result";

    private static final int ACTIVITY_RESULT_QR_DRDROID = 500;
    private ViewPagerFragment _viewPagerFragment;


    private Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        createPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        _menu = menu;
        getSupportActionBar().setTitle("Gefriertruhen App");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml. 
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.add){
            openQRDroid();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openQRDroid(){
        //Create a new Intent to send to QR Droid
        Intent qrDroid = new Intent( SCAN ); //Set action "la.droid.qr.scan"

        //Send intent and wait result
        try {
            startActivityForResult(qrDroid, ACTIVITY_RESULT_QR_DRDROID);
        } catch (ActivityNotFoundException activity) {
            qrDroidRequired(this);
        }
    }

    /**
     * Display a message stating that QR Droid is requiered, and lets the user download it for free
     * @param activity
     */
    public static void qrDroidRequired( final Activity activity ) {
        //Apparently, QR Droid is not installed, or it's previous to version 3.5
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Kein QR Droid")
                .setCancelable(true)
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Google Play", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=la.droid.qr")));
                    }
                })
                .setNeutralButton("QR Droid Download", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://droid.la/apk/qr/")));
                    }
                });
        builder.create().show();
    }

    public void changeFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(
                R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right
        ).replace(R.id.main_layout, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && data.getExtras() != null) {
            String result = data.getExtras().getString(RESULT);
            showItemDialog(result);
        }
    }
    private int type = 0;
    private  void showItemDialog(final String barCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setSingleChoiceItems(new String[]{"Gefriertruhe", "Lager"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                type = which;
            }
        });
        builder.setTitle("Art wählen");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(type == 0){
                    FridgeItem item = DataBaseSingleton.getInstance().getFridgeItem(barCode);
                    if(item == null) {
                        item = new FridgeItem(barCode, 1, null, barCode);
                    }
                    FridgeDetailFragment fragment = new FridgeDetailFragment();
                    fragment.setData(item);
                    changeFragment(fragment, true);
                } else {
                    ShelfItem item = DataBaseSingleton.getInstance().getShelfItem(barCode);
                    if(item == null){
                        item = new ShelfItem(barCode, 1, null, barCode, 1);
                    }
                    ShelfDetailFragment fragment = new ShelfDetailFragment();
                    fragment.setData(item);
                    changeFragment(fragment, true);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }


    public void createPage() {
        DataBaseSingleton.getInstance().loadDataBase(getBaseContext());
        _viewPagerFragment = new ViewPagerFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_layout, _viewPagerFragment).commit();
        Intent intent = new Intent(this, BackgroundNotificationService.class);
        startService(intent);
    }
    public Menu get_menu() {
        return _menu;
    }
}
