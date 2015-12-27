package com.example.student.gefriertruhapp.UPC;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JsonResult {

    @SerializedName("valid")
    @Expose
    private String valid;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("itemname")
    @Expose
    private String itemname;
    @SerializedName("alias")
    @Expose
    private String alias;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("avg_price")
    @Expose
    private String avgPrice;
    @SerializedName("rate_up")
    @Expose
    private int rateUp;
    @SerializedName("rate_down")
    @Expose
    private int rateDown;
    @SerializedName("reason")
    @Expose
    private String reason;


    /**
     * No args constructor for use in serialization
     */
    public JsonResult() {
    }

    /**
     * @return The valid
     */
    public String getValid() {
        return valid;
    }

    /**
     * @param valid The valid
     */
    public void setValid(String valid) {
        this.valid = valid;
    }

    /**
     * @return The number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return The itemname
     */
    public String getItemname() {
        return itemname;
    }

    /**
     * @param itemname The itemname
     */
    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    /**
     * @return The alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias The alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The avgPrice
     */
    public String getAvgPrice() {
        return avgPrice;
    }

    /**
     * @param avgPrice The avg_price
     */
    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    /**
     * @return The rateUp
     */
    public int getRateUp() {
        return rateUp;
    }

    /**
     * @param rateUp The rate_up
     */
    public void setRateUp(int rateUp) {
        this.rateUp = rateUp;
    }

    /**
     * @return The rateDown
     */
    public int getRateDown() {
        return rateDown;
    }

    /**
     * @param rateDown The rate_down
     */
    public void setRateDown(int rateDown) {
        this.rateDown = rateDown;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}