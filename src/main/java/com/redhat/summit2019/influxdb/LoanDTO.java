package com.redhat.summit2019.influxdb;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "loan")
public class LoanDTO {

    @Column(name = "time")
    private Instant time;

    @Column(name = "applicationID")
    private int applicationID;

    @Column(name = "farmCity", tag = true)
    private String farmCity;

    @Column(name = "farmCouncil", tag = true)
    private String farmCouncil;

    @Column(name = "loanStatus")
    private String loanStatus;
    
    @Column(name = "loanAmount")
    private long loanAmount;
    
    

    public long getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getFarmCity() {
        return farmCity;
    }

    public void setFarmCity(String farmCity) {
        this.farmCity = farmCity;
    }

    public String getFarmCouncil() {
        return farmCouncil;
    }

    public void setFarmCouncil(String farmCouncil) {
        this.farmCouncil = farmCouncil;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
}
