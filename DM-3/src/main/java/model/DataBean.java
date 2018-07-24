package model;

import org.json.JSONObject;

import java.io.Serializable;

public class DataBean implements Serializable {

	public String fileName;

	public float forceValue;

	public float disValue;

	public int isTrue;

	public String operator;

	public String location;

	public String liftid;

	public DataBean(String fileName, float forceValue,float disValue, int isTrue, String liftid, String location, String operator ) {
		this.fileName = fileName;
		this.liftid = liftid;
		this.location = location;
		this.operator = operator;
		this.isTrue = isTrue;
		this.disValue = disValue;
		this.forceValue = forceValue;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLiftid() {
		return liftid;
	}

	public void setLiftid(String liftid) {
		this.liftid = liftid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getIsTrue() {
		return isTrue;
	}

	public void setIsTrue(int isTrue) {
		this.isTrue = isTrue;
	}

	public float getDisValue() {
		return disValue;
	}

	public void setDisValue(float disValue) {
		this.disValue = disValue;
	}

	public float getForceValue() {
		return forceValue;
	}

	public void setForceValue(float forceValue) {
		this.forceValue = forceValue;
	}
}
