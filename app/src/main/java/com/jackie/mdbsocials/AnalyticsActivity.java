/** Represents the detailed version of a Social.
 * @author: Jacqueline Zhang
 * @date: 03/05/2019
 */
package com.jackie.mdbsocials;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.jackie.mdbsocials.FirebaseUtils.getSocialsDatabaseRef;

public class AnalyticsActivity extends AppCompatActivity {
    /** Bar Chart for Socials Per Month in 2019. */
    private BarChart _socialsPerMonth;
    /** Line Chart for Interested Per Month in 2019. */
    private LineChart _interestedPerMonth;

    /** Entries for Socials Per Month bar chart. */
    private ArrayList<BarEntry> _barEntries = new ArrayList<>();
    /** Entries for Interested Per Month line chart. */
    private ArrayList<Entry> _lineEntries = new ArrayList<>();

    /** Month labels for both charts. */
    private ArrayList<String> _monthLabels = new ArrayList<>();

    /** Data per month for Socials Per Month bar chart. */
    private int[] _socialsData = new int[12];
    /** Data per month for Interested Per Month line chart. */
    private int[] _interestedData = new int[12];

    /** Firebase Database Reference. */
    private DatabaseReference _mDatabase;

    /** Sets up activity. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Toolbar.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Initializing variables.
        _socialsPerMonth = findViewById(R.id.barChart);
        _interestedPerMonth = findViewById(R.id.lineChart);
        _mDatabase = getSocialsDatabaseRef();

        // Draws the charts.
        setUpChart();
        drawCharts();
        createMonthLabels();
        setMonthLabels(_socialsPerMonth);
        setMonthLabels(_interestedPerMonth);


    }

    /** Creates the Socials Per Month Bar Chart. */
    void drawCharts() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _socialsData = new int[12];
                _interestedData = new int[12];
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String year = date.substring(6);
                    int interested = Integer.valueOf(snapshot.child("interested").getValue().toString());

                    // Ensures that we are only looking at socials in 2019.
                    if (year.equals("2019")) {
                        int month = Integer.parseInt(date.substring(0, 2));
                        // Must decrease by one because of indexing (January starts at 1 but indexing starts at 0).
                        _socialsData[month - 1] += 1;
                        _interestedData[month - 1] += interested;
                    }

                }
                _barEntries.clear();
                _lineEntries.clear();
                for (int i = 0; i < 12; i++) {
                    _barEntries.add(new BarEntry(i, _socialsData[i]));
                    _lineEntries.add(new Entry(i, _interestedData[i]));
                }
                // For Bar Chart
                BarDataSet barDataSet = new BarDataSet(_barEntries, "Socials");
                BarData barData = new BarData(barDataSet);
                _socialsPerMonth.setData(barData);
                _socialsPerMonth.notifyDataSetChanged();
                _socialsPerMonth.invalidate();

                // For Line Chart
                LineDataSet lineDataSet = new LineDataSet(_lineEntries, "Interested");
                LineData lineData = new LineData(lineDataSet);
                _interestedPerMonth.setData(lineData);
                _interestedPerMonth.notifyDataSetChanged();
                _interestedPerMonth.invalidate();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        _mDatabase.addValueEventListener(postListener);
    }

    /** Sets up both charts. Mainly for visual purposes. */
    void setUpChart() {
        // Sets up bar chart.
        _socialsPerMonth.setDescription(null);
        _socialsPerMonth.animateY(1000);
        _socialsPerMonth.getAxisLeft().setDrawGridLines(false);
        _socialsPerMonth.getXAxis().setDrawGridLines(false);
        _socialsPerMonth.getAxisRight().setDrawGridLines(false);

        // Sets up line chart.
        _interestedPerMonth.setDescription(null);
        _interestedPerMonth.animateY(1000);
        _interestedPerMonth.getAxisLeft().setDrawGridLines(false);
        _interestedPerMonth.getXAxis().setDrawGridLines(false);
        _interestedPerMonth.getAxisRight().setDrawGridLines(false);
    }


    /** Creates the month labels. */
    void createMonthLabels() {
        _monthLabels.add("Jan");
        _monthLabels.add("Feb");
        _monthLabels.add("Mar");
        _monthLabels.add("Apr");
        _monthLabels.add("May");
        _monthLabels.add("Jun");
        _monthLabels.add("Jul");
        _monthLabels.add("Aug");
        _monthLabels.add("Sept");
        _monthLabels.add("Oct");
        _monthLabels.add("Nov");
        _monthLabels.add("Dec");
    }

    /** Sets the labels of the charts with the months.
     * @param: C represents either the line chart or bar chart. */
    void setMonthLabels(Chart c) {
        XAxis xAxis = c.getXAxis();
        xAxis.setLabelCount(12, true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return _monthLabels.get((int) value);
            }
        });
    }

}
