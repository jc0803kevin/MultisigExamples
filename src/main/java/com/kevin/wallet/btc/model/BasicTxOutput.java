package com.kevin.wallet.btc.model;

import java.math.BigDecimal;

public class BasicTxOutput {

    public String address;
    public BigDecimal amount;
    public byte[] data;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
