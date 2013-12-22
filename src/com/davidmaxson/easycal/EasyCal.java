package com.davidmaxson.easycal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class EasyCal extends Activity {

	String[] daysOfWeekShort = new String[] { "", "Sun", "Mon", "Tue", "Wed",
			"Thu", "Fri", "Sat" };

	Calendar selectedDate = null, centerDate = null;

	TextView lblLess3, lblLess2, lblLess1, lblDay0, lblMore1, lblMore2,
			lblMore3, lblSelectedDay;
	EditText txtNotes;
	Button btnLess3, btnLess2, btnLess1, btnDay0, btnMore1, btnMore2, btnMore3,
			btnLeft, btnRight, btnReturnToday;

	SimpleDateFormat selectedDateFormatter = new SimpleDateFormat(
			"EEEE MMMM dd, yyyy"), sqlDateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");

	DataBaseHelper dbNotes;
	private final String SELECT = "SELECT note FROM Notes WHERE date = ?",
			UPDATE = "UPDATE Notes SET note = ? WHERE date = ?",
			INSERT = "INSERT INTO Notes (date, note) VALUES (?,?)",
			DELETE = "DELETE FROM Notes WHERE date = ?",
			GET_CHANGES = "SELECT changes()",
			RESET_DB = "DELETE FROM Notes",
			GET_TABLES = "SELECT * FROM sqlite_master WHERE type='table' AND name='Notes'",
			CREATE_DB = "CREATE TABLE Notes ( date DATE PRIMARY KEY, note VARCHAR )";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easy_cal);

		// OPEN NOTES DATABASE

		try {
			dbNotes = new DataBaseHelper(this.getApplicationContext());

		} catch (IOException e) {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
			dlgAlert.setMessage("There was an error opening the database");
			dlgAlert.setTitle("ERROR");
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			finish();
		}
		try {
			Cursor tables = sqlQuery(GET_TABLES, new String[0]);
			if (tables.getCount() == 0)
				sqlAction(CREATE_DB, new String[0]);
			tables.close();

		} catch (SQLException e) {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
			dlgAlert.setMessage("There was an error building the tables needed");
			dlgAlert.setTitle("ERROR");
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			finish();
		}

		// INITIALIZE VARIABLES

		lblLess3 = (TextView) findViewById(R.id.lblLess3);
		lblLess2 = (TextView) findViewById(R.id.lblLess2);
		lblLess1 = (TextView) findViewById(R.id.lblLess1);
		lblDay0 = (TextView) findViewById(R.id.lblDay0);
		lblMore1 = (TextView) findViewById(R.id.lblMore1);
		lblMore2 = (TextView) findViewById(R.id.lblMore2);
		lblMore3 = (TextView) findViewById(R.id.lblMore3);

		btnLess3 = (Button) findViewById(R.id.btnLess3);
		btnLess2 = (Button) findViewById(R.id.btnLess2);
		btnLess1 = (Button) findViewById(R.id.btnLess1);
		btnDay0 = (Button) findViewById(R.id.btnDay0);
		btnMore1 = (Button) findViewById(R.id.btnMore1);
		btnMore2 = (Button) findViewById(R.id.btnMore2);
		btnMore3 = (Button) findViewById(R.id.btnMore3);

		btnLeft = (Button) findViewById(R.id.btnLeft);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnReturnToday = (Button) findViewById(R.id.btnReturnToday);

		txtNotes = (EditText) findViewById(R.id.txtNotes);
		lblSelectedDay = (TextView) findViewById(R.id.lblSelectedDate);

		// BEGINNING OF EVENT HANDLERS

		btnLess3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(-3);
			}
		});
		btnLess2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(-2);
			}
		});
		btnLess1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(-1);
			}
		});
		btnDay0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(0);
			}
		});
		btnMore1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(1);
			}
		});
		btnMore2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(2);
			}
		});
		btnMore3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(3);
			}
		});
		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(-7);
			}
		});
		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeDay(7);
			}
		});
		btnReturnToday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				centerDate = Calendar.getInstance();
				setNewDay((Calendar)centerDate.clone());
			}
		});

		// GET THINGS READY FOR THE CURRENT DAY

		setNewDay(Calendar.getInstance());
	}

	private void changeDay(int changeInDays) {
		
		Calendar cal = (Calendar) centerDate.clone();
		cal.add(Calendar.DATE, changeInDays);
		if(Math.abs(changeInDays) > 3)
			centerDate = (Calendar)cal.clone();
		setNewDay(cal);
		
	}

	private void setNewDay(Calendar newDate) {
		if (selectedDate != null) {
			saveCurrentNotes();
		}

		selectedDate = newDate;
		if(centerDate == null)
			centerDate = (Calendar)selectedDate.clone();

		// Go from day-3 to day+3
		Calendar tmpDate = (Calendar) centerDate.clone();
		tmpDate.add(Calendar.DATE, -3);

		btnLess3.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblLess3.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		if(!"".equals(sqlSelect(tmpDate)))
			btnLess3.setTextColor(0xFF0000FF);
		else
			btnLess3.setTextColor(0xFF000000);
		tmpDate.add(Calendar.DATE, 1);

		btnLess2.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblLess2.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		if(!"".equals(sqlSelect(tmpDate)))
			btnLess2.setTextColor(0xFF0000FF);
		else
			btnLess2.setTextColor(0xFF000000);
		tmpDate.add(Calendar.DATE, 1);

		btnLess1.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblLess1.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		if(!"".equals(sqlSelect(tmpDate)))
			btnLess1.setTextColor(0xFF0000FF);
		else
			btnLess1.setTextColor(0xFF000000);
		tmpDate.add(Calendar.DATE, 1);

		btnDay0.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblDay0.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		lblSelectedDay.setText(selectedDateFormatter.format(selectedDate.getTime()));
		if(!"".equals(sqlSelect(tmpDate)))
			btnDay0.setTextColor(0xFF0000FF);
		else
			btnDay0.setTextColor(0xFF000000);
		tmpDate.add(Calendar.DATE, 1);

		btnMore1.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblMore1.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		if(!"".equals(sqlSelect(tmpDate)))
			btnMore1.setTextColor(0xFF0000FF);
		else
			btnMore1.setTextColor(0xFF000000);
		tmpDate.add(Calendar.DATE, 1);

		btnMore2.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblMore2.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		if(!"".equals(sqlSelect(tmpDate)))
			btnMore2.setTextColor(0xFF0000FF);
		else
			btnMore2.setTextColor(0xFF000000);
		tmpDate.add(Calendar.DATE, 1);

		btnMore3.setText(Integer.toString(tmpDate.get(Calendar.DAY_OF_MONTH)));
		lblMore3.setText(daysOfWeekShort[tmpDate.get(Calendar.DAY_OF_WEEK)]);
		if(!"".equals(sqlSelect(tmpDate)))
			btnMore3.setTextColor(0xFF0000FF);
		else
			btnMore3.setTextColor(0xFF000000);

		loadCurrentNotes();
	}

	private void loadCurrentNotes() {
		txtNotes.setText(sqlSelect(selectedDate));
	}

	private void saveCurrentNotes() {
		String text = txtNotes.getText().toString();
		if (!"".equals(text)) {
			sqlUpdate(selectedDate, text);
		} else {
			sqlDelete(selectedDate);
		}
	}

	private String sqlSelect(Calendar date) {
		Cursor cu = sqlQuery(SELECT, new String[] { sqlFormatDate(date) });
		String toReturn = cu.moveToFirst() ? cu.getString(0) : "";
		cu.close();
		return toReturn;
	}

	private void sqlUpdate(Calendar date, String notes) {
		// TODO: Execute update
		sqlAction(UPDATE, new String[] { notes, sqlFormatDate(date) });
		Cursor cu = sqlQuery(GET_CHANGES, new String[] {});
		if (cu.moveToFirst() && cu.getInt(0) == 0) {
			// TODO: If nothing updated, insert
			sqlAction(INSERT, new String[] { sqlFormatDate(date), notes });
		}
		cu.close();
	}

	private void sqlDelete(Calendar date) {
		sqlAction(DELETE, new String[] { sqlFormatDate(date) });
	}

	private String sqlFormatDate(Calendar date) {
		return sqlDateFormatter.format(date.getTime());
	}

	private Cursor sqlQuery(String query, String[] args) {
		try {
			return dbNotes.myDataBase.rawQuery(query, args);
		} catch (SQLException e) {
			System.out.println(e.toString());
			return null;
		}
	}

	private void sqlAction(String query, String[] args) {
		dbNotes.myDataBase.execSQL(query, args);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.easy_cal, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		saveCurrentNotes();
		dbNotes.close();
		super.onDestroy();

	}
}
