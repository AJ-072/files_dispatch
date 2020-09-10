package com.aj.filesdispatch.nav_optn;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.aj.filesdispatch.R;

public class HelpandFeed extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView helpView;
    ArrayAdapter<String> adapter;
    String[] listItem = {"How to Use Files Dispatch"};
    Button feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feed);
        helpView = findViewById(R.id.help);
        feed = findViewById(R.id.feedbutton);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, listItem);
        helpView.setAdapter(adapter);
        helpView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}