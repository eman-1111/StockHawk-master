package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;


/**
 * Created by Eman on 10/8/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String SELECTED_STOCK = "selected_stock";
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] STOCK_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.ISUP,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns._ID
    };
    // these indices must match the projection
    static final int INDEX_SYMBOL = 0;
    static final int INDEX_BIDPRICE = 1;
    static final int INDEX_ISUP = 2;
    static final int INDEX_PERCENT_CHANGE = 3;
    static final int INDEX_CHANGE = 4;
    static final int INDEX_ID = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        STOCK_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);


                String symbol = data.getString(INDEX_SYMBOL);
                views.setTextViewText(R.id.stock_symbol_widget, symbol);
                views.setContentDescription(R.id.stock_symbol_widget,
                        getBaseContext().getString(R.string.a11y_stock_symbol, symbol));


                String bidBrice = data.getString(INDEX_BIDPRICE);
                views.setTextViewText(R.id.bid_price_widget, bidBrice);
                views.setContentDescription(R.id.bid_price_widget,
                        getBaseContext().getString(R.string.a11y_stock_bid_price, bidBrice));


                if (data.getInt(INDEX_ISUP) == 1) {
                    views.setInt(R.id.change, getResources().
                            getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_green);

                } else {
                    views.setInt(R.id.change, getResources().
                            getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_red);
                }


                if (Utils.showPercent) {
                    String value = data.getString(INDEX_PERCENT_CHANGE);
                    views.setTextViewText(R.id.change_widget, value);
                    views.setContentDescription(R.id.change_widget,
                            getBaseContext().getString(R.string.a11y_percent_change, value));
                } else {
                    String value = data.getString(INDEX_CHANGE);
                    views.setTextViewText(R.id.change_widget, value);
                    views.setContentDescription(R.id.change_widget,
                            getBaseContext().getString(R.string.a11y_stock_change, value));

                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(SELECTED_STOCK, symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }


            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
