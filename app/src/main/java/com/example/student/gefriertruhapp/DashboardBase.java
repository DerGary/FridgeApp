package com.example.student.gefriertruhapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.example.student.gefriertruhapp.FridgeList.FridgeDetailFragment;
import com.example.student.gefriertruhapp.Helper.Action;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.FridgeList.FridgeListViewPagerFragment;
import com.example.student.gefriertruhapp.Serialization.FileAccess;

public abstract class DashboardBase extends ActionBarActivity {
    protected FridgeListViewPagerFragment _fridgeListViewPagerFragment;
    protected Menu _menu;

    //Actions
    public static final String SCAN = "la.droid.qr.scan";
    public static final String ENCODE = "la.droid.qr.encode";
    public static final String DECODE = "la.droid.qr.decode";

    //Parameters
    //SCAN / DECODE
    public static final String COMPLETE = "la.droid.qr.complete"; //Default: false
    //ENCODE
    public static final String CODE = "la.droid.qr.code"; //Required
    public static final String SIZE = "la.droid.qr.size"; //Default: Fit screen
    //ENCODE / DECODE
    public static final String IMAGE = "la.droid.qr.image"; //Default for encode: false / Required for decode

    //Result
    public static final String RESULT = "la.droid.qr.result";
    //Alternative Result String
    public static final String ALTERNATE_RESULT = "SCAN_RESULT";

    protected static final int ACTIVITY_RESULT_QRDROID_ADD = 500;
    protected static final int ACTIVITY_RESULT_QRDROID_DEL = 600;
    protected static final int ACTIVITY_RESULT_QRDROID_OPEN = 700;
    public static final int ACTIVITY_RESULT_QRDROID_DO_INVENTORY = 800;


    public void openQRDroid(int requestCode) {
        //Create a new Intent to send to QR Droid
        Intent qrDroid = new Intent(SCAN); //Set action "la.droid.qr.scan"
        qrDroid.putExtra("la.droid.qr.complete" , true);
        //Send intent and wait result
        try {
            startActivityForResult(qrDroid, requestCode);
        } catch (ActivityNotFoundException activity) {
            showQrDroidRequiredAlert(this);
        }
    }

    /**
     * Display a message stating that QR Droid is requiered, and lets the user download it for free
     *
     * @param activity
     */
    public static void showQrDroidRequiredAlert(final Activity activity) {
        //Apparently, QR Droid is not installed, or it's previous to version 3.5
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.no_qr_droid_installed)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.google_play, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=la.droid.qr")));
                    }
                })
                .setNeutralButton(R.string.qr_droid_download, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://droid.la/apk/qr/")));
                    }
                });
        builder.create().show();
    }

    public static void showNoStoreCreatedAlert(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.no_stock_set_message)
                .setCancelable(true)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
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
        else if (!_fridgeListViewPagerFragment.onBackPressed())
            super.onBackPressed();
    }


    public Menu get_menu() {
        return _menu;
    }

    protected void setNameOfFridgeItemAndNavigateToDetail(FridgeItem item){
        if(item.getBarCode() != null && !item.getBarCode().isEmpty()){
            String name = DataBaseSingleton.getInstance().getNameByBarcode(item.getBarCode());
            if(name != null && !name.isEmpty()){
                item.setName(name);
            }
        }
        navigateToDetailFragment(item);
    }

    protected void navigateToDetailFragment(FridgeItem item) {
        FridgeDetailFragment fragment = new FridgeDetailFragment();
        fragment.setData(item);
        changeFragment(fragment, true);
    }

    protected void colorDialog(Dialog dialog, Action action) {
        if (action == Action.ADD) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 223, 255, 233)));
        } else if (action == Action.DELETE) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 255, 200, 200)));
        }
    }

    public abstract void createPage();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FileAccess.REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted

                    createPage();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(R.string.no_sd_rights_exit_message);
                    final Activity activity = this;
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    });
                    dialog.create().show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
