//http://stackoverflow.com/questions/5086962/adding-your-own-sqlite-database-to-an-android-application
package com.davidmaxson.easycal;

import java.io.*;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.*;

public class DataBaseHelper extends SQLiteOpenHelper {
	// private static final String DB_PATH =
	// "/data/data/com.davidmaxson.easycal/databases/";

	private Context mycontext;

	private String DB_PATH = "";
	private static String DB_NAME = "notes.sqlite";// the extension may
													// be .sqlite or .db
	public SQLiteDatabase myDataBase;

	/*
	 * private String DB_PATH = "/data/data/" +
	 * mycontext.getApplicationContext().getPackageName() + "/databases/";
	 */

	public DataBaseHelper(Context context) throws IOException {
		super(context, DB_NAME, null, 1);
		this.mycontext = context;
		boolean dbexist = checkdatabase();
		if (dbexist) {
			// System.out.println("Database exists");
			opendatabase();
		} else {
			System.out.println("Database doesn't exist");
			createdatabase();
		}

	}

	public void createdatabase() throws SQLException {
		myDataBase = this.getWritableDatabase();
	}

	private boolean checkdatabase() {
		// SQLiteDatabase checkdb = null;
		try {
			this.getWritableDatabase();
		} catch (SQLiteException e) {
			System.out.println("Database doesn't exist");
			return false;
		}

		return true;
	}

	public void opendatabase() throws SQLException {
		// Open the database
		myDataBase = this.getWritableDatabase();
	}

	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
}