package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by Eman on 9/26/2016.
 */
public class ChartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int CURSOR_LOADER_ID = 0;
    private  String searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_graph);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        Intent intent = getIntent();

        if(intent.getStringExtra("SearchValue") != null){
            searchKey = intent.getStringExtra("SearchValue");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        // This narrows the return to only the stocks that are most current.
        if(searchKey != null){
            return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE},
                    QuoteColumns.SYMBOL + " = ?",
                    new String[]{searchKey},
                    null);

        }


        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        Log.e("Cursor", data.toString());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
    }

}
