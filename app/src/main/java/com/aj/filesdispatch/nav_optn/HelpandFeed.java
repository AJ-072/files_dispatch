package com.aj.filesdispatch.nav_optn;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.aj.filesdispatch.R;

public class HelpandFeed extends AppCompatActivity {
    ListView helpview;
    ArrayAdapter<String> adapter;
    String[] listitem = {"How to Use Files Dispatch"};
    Button feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feed);
        helpview = findViewById(R.id.help);
        feed = findViewById(R.id.feedbutton);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, listitem);
        helpview.setAdapter(adapter);
    }

}