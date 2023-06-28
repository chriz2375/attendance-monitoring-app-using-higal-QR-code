package com.example.sdeproject2;

public class EmployeeModel {
    private int id;
    private String name;
    private String address;
    private long number;
    private String purpose;
    private String date_time;


    public EmployeeModel(int id, String name, String address, long number, String purpose, String date_time) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.number = number;
        this.purpose = purpose;
        this.date_time = date_time;
    }

    public EmployeeModel() {
    }

    @Override
    public String toString() {
        String s = "{" +
                ", Client no: '" + id + '\'' +
                ", \nName: '" + name + '\'' +
                ", \nAddress: '" + address + '\'' +
                ", \nContact no: '" + number + '\'' +
                ", \nAppointment: '" + purpose + '\'' +
                ", \nDate&Time: '" + date_time + '\'' +
                '}';
        return s;
    }

    //getter and setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public long getNumber() {
        return number;
    }
    public void setNumber(int number) { this.number = number; }

    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDateTime() {
        return date_time;
    }
    public void setDate_time(String purpose) {
        this.date_time = date_time;
    }


}
