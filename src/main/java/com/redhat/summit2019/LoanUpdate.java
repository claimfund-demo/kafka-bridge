package com.redhat.summit2019;

import java.util.Objects;

public class LoanUpdate {
    private String farmCity;
    private String farmCouncil;
    private String loanStatus;
    private long loanAmount;

    public LoanUpdate(String farmCity, String farmCouncil, String loanStatus, long loanAmount) {
        this.farmCity = farmCity;
        this.farmCouncil = farmCouncil;
        this.loanStatus = loanStatus;
        this.loanAmount = loanAmount;
    }

    public LoanUpdate() {
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

    public long getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(long loanAmount) {
        this.loanAmount = loanAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanUpdate that = (LoanUpdate) o;
        return getLoanAmount() == that.getLoanAmount() &&
                getFarmCity().equals(that.getFarmCity()) &&
                getFarmCouncil().equals(that.getFarmCouncil()) &&
                getLoanStatus().equals(that.getLoanStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFarmCity(), getFarmCouncil(), getLoanStatus(), getLoanAmount());
    }

    @Override
    public String toString() {
        return "LoanUpdate{" +
                "farmCity='" + farmCity + '\'' +
                ", farmCouncil='" + farmCouncil + '\'' +
                ", loanStatus='" + loanStatus + '\'' +
                ", loanAmount=" + loanAmount +
                '}';
    }
}
