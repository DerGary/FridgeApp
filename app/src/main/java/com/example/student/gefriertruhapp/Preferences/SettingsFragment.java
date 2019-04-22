package com.example.student.gefriertruhapp.Preferences;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.History.HistoryViewPagerFragment;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Serialization.CSVHelper;
import com.example.student.gefriertruhapp.StoreList.StoresListFragment;

/**
 * Created by Stefan on 05-06-17.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Preference storageSettings = (Preference) findPreference("storagesettings");
        storageSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ((Dashboard)getActivity()).changeFragment(new StoresListFragment(), true);
                return true;
            }
        });

        Preference csvExportPreference = (Preference) findPreference("csvexport");
        csvExportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                saveCSV();
                return true;
            }
        });

        Preference historyPreference = (Preference) findPreference("history");
        historyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ((Dashboard)getActivity()).changeFragment(new HistoryViewPagerFragment(), true);
                return true;
            }
        });
    }

    private void saveCSV() {
        for (Store store : DataBaseSingleton.getInstance().getStores()) {
            CSVHelper.writeStore(this.getActivity(), store);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.done);
        builder.setMessage(R.string.export_done_message);
        builder.setNeutralButton(R.string.ok, null);
        builder.create().show();
    }
}

