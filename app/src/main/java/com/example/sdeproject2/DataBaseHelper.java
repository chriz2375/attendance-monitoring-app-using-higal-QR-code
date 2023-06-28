package com.example.sdeproject2;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    static int idRec[] = new int[20];
    String CLIENT_TABLE = "CLIENT_TABLE";
    public static final String COLUMN_EMPLOYEE_FULLNAME  = "EMPLOYEE_FULLNAME";
    public static final String COLUMN_EMPLOYEE_ADDRESS = "EMPLOYEE_ADDRESS";
    public static final String COLUMN_EMPLOYEE_NUMBER = "EMPLOYEE_NUMBER";
    public static final String COLUMN_EMPLOYEE_PURPOSE = "EMPLOYEE_PURPOSE";
    public static final String COLUMN_EMPLOYEE_DATE_TIME = "EMPLOYEE_DATE_TIME";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "Client.db", null, 1 );
    }

    // this is called the first time a database accessed. There should be code for creating a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + CLIENT_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMPLOYEE_FULLNAME + " TEXT, "
                + COLUMN_EMPLOYEE_ADDRESS + " TEXT, "
                + COLUMN_EMPLOYEE_NUMBER + " INT, "
                + COLUMN_EMPLOYEE_PURPOSE + " TEXT, "
                + COLUMN_EMPLOYEE_DATE_TIME + " TEXT )";
        db.execSQL(createTableStatement);
    }

    // this is called if the database version number changes. It prevent previous users apps from breaking when you change the database design
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
    public boolean addOne(EmployeeModel employeeModel){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, employeeModel.getId());
        cv.put(COLUMN_EMPLOYEE_FULLNAME, employeeModel.getName());
        cv.put(COLUMN_EMPLOYEE_ADDRESS, employeeModel.getAddress());
        cv.put(COLUMN_EMPLOYEE_NUMBER, employeeModel.getNumber());
        cv.put(COLUMN_EMPLOYEE_PURPOSE, employeeModel.getPurpose());
        cv.put(COLUMN_EMPLOYEE_DATE_TIME, employeeModel.getDateTime());

        long insert = db.insert(CLIENT_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else {
            return true;
        }

    }

    public List<EmployeeModel> getEveryone(){
        List<EmployeeModel> returnList = new ArrayList<>();

        //get data from the database

        String queryString = "SELECT  *  FROM " + CLIENT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()){
            //loop through the cursor
            do{
                int clientID = cursor.getInt(0);
                String clientName = cursor.getString(1);
                String clientAddress = cursor.getString(2);
                long clientNumber = cursor.getLong(3);
                String clientPurpose = cursor.getString(4);
                String clientDateTime = cursor.getString(5);
                EmployeeModel newClient = new EmployeeModel(clientID, clientName, clientAddress, clientNumber,clientPurpose,clientDateTime);
                //String newClient = "Client no. " + clientID + "\tName: " + clientName + "\nAddress: " + clientAddress
                //+"\nContact no. " + clientNumber + "\nAppointment: " + clientPurpose + "\nDate and Time: " + clientDateTime;
                returnList.add(newClient);
            }
            while (cursor.moveToNext());
        }
        else{
            //failed to add
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public int scanID(){
        int id = 0;
        String sql1 = "SELECT ID FROM " + CLIENT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql1, null);
        for(int i=0;cursor.moveToNext();i++) {
            idRec[i] = cursor.getInt(0);
            if (idRec[0]==0) {
                break;
            }
            else
                id = idRec[i];
        }
        if (id==0){
            id=1;
        }
        else {
            id++;
        }

        cursor.close();
        db.close();
        return id;
    }


    public boolean deleteOne(EmployeeModel employeeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql1 = "DELETE FROM " + CLIENT_TABLE + " WHERE " + COLUMN_ID + " = " + employeeModel.getId();
        Cursor cursor = db.rawQuery(sql1, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean deleteAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql1 = "DELETE FROM " + CLIENT_TABLE;
        Cursor cursor = db.rawQuery(sql1, null);
        if (cursor.moveToFirst()) {
            return false;
        }
        else {
            return true;
        }

    }


    public String exportDatabase(String dateTime) {
        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return "error 1";
        }
        else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                file = new File(exportDir, dateTime + ".xlsx");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                /**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 */
                SQLiteDatabase db = this.getReadableDatabase(); //open the database for reading

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */
                Cursor curCSV = db.rawQuery("select * from " + CLIENT_TABLE, null);
                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                printWriter.println("Records of " + dateTime);
                printWriter.println("ID \t NAME \t ADDRESS \t MOBILE NUMBER \t  APPOINTMENT \t  DATE AND TIME");
                while(curCSV.moveToNext())
                {
                    String number;
                    int clientID = curCSV.getInt(0);
                    String clientName = curCSV.getString(1);
                    String clientAddress = curCSV.getString(2);
                    long clientNumber =  curCSV.getLong(3);
                    String clientPurpose = curCSV.getString(4);
                    String clientDateTime = curCSV.getString(5);

                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    String record = clientID + "\t" + clientName + "\t" + clientAddress + "\t"
                            + clientNumber+ "\t" + clientPurpose + "\t" + clientDateTime;
                    printWriter.println(record); //write the record in the .csv file
                }

                curCSV.close();
                db.close();


            }

            catch(Exception exc) {
                //if there are any exceptions, return false
                return exc.toString();
            }
            finally {
                if(printWriter != null) printWriter.close();
            }

            //If there are no errors, return true.
            return "yes";
        }
    }

}







