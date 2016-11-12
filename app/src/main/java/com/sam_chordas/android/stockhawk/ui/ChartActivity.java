package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.touch_helper.Constants;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Eman on 9/26/2016.
 */
public class ChartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int CURSOR_LOADER_ID = 1;
    private Cursor mCursor;
    public final String LOG_TAG = ChartActivity.class.getSimpleName();
    String searchKey;
    LineChartView lineChartView;
    private LineSet mLineSet;
    int maxRange, minRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mLineSet = new LineSet();
        lineChartView = (LineChartView) findViewById(R.id.line_chart);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            searchKey = (String) bundle.get(Constants.SELECTED_STOCK);
        }
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
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
        } else if (id == R.id.home) {

            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (searchKey != null) {
            return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns.BIDPRICE, QuoteColumns.CHANGE},
                    QuoteColumns.SYMBOL + " = ?",
                    new String[]{searchKey},
                    null);

        }


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mCursor = data;
            findRange(mCursor);
            initLineChart();
            mCursor.moveToFirst();

            for (int i = 0; i < mCursor.getCount(); i++) {
                float value = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
                mLineSet.addPoint(" " + i, value);
                mCursor.moveToNext();
            }
            mLineSet.setColor(getResources().getColor(R.color.colorGray))
                    .setDotsStrokeThickness(Tools.fromDpToPx(2))
                    .setDotsStrokeColor(getResources().getColor(R.color.colorLightBlue));
            lineChartView.addData(mLineSet);
            lineChartView.show();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void initLineChart() {
        Paint gridPaint = new Paint();
        gridPaint.setColor(getResources().getColor(R.color.colorLightBlue));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));
        lineChartView.setBorderSpacing(1)
                .setAxisBorderValues(minRange - 1, maxRange + 1, 1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(getResources().getColor(R.color.colorLightBlue))
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(2))
                .setGrid(ChartView.GridType.HORIZONTAL, gridPaint);
    }

    public void findRange(Cursor mCursor) {
        ArrayList<Float> mArrayList = new ArrayList<Float>();
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE))));
        }
        maxRange = Math.round(Collections.max(mArrayList));
        minRange = Math.round(Collections.min(mArrayList));


    }

}

