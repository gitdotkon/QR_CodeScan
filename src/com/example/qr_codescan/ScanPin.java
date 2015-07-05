package com.example.qr_codescan;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;

public class ScanPin {

	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ScanPin(String pin, String string, String location) {
		super();
		this.pin = pin;
		this.scanTime = string;
		this.location = location;
	}

	public ScanPin(String pin,String location) {
		// super();
		// this.pin = pin;
		// this.scanTime = scanTime;
		// this.location = location;
		this(pin, sdf.format(new Date()), location);
		// this(pin,scanTime,"");
	}

	private String pin;

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    private String scanTime;
	private String location;
    private String dealer;


	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getScanTime() {
		return scanTime;
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int hashCode() {
		return this.getPin().hashCode();

	}

	public boolean equals(Object obj) {
		// System.out.println(this.name+"...equals()");
		ScanPin sp = (ScanPin) obj;
		return this.pin == sp.pin;
	}

	@Override
	public String toString() {
		return pin + "," + scanTime + "," + location +";" ;
	}

}
