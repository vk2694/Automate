package com.lsv.automate.data;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "automation")
public class Location {
    @PrimaryKey
    @NonNull
    private Integer id;
    private Double lat;
    private Double lng;
    private Boolean bluethooth;
    private Boolean donotdistrub;
    private String label;
    private Integer mediaVolume;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Boolean getBluethooth() {
        return bluethooth;
    }

    public void setBluethooth(Boolean bluethooth) {
        this.bluethooth = bluethooth;
    }

    public Boolean getDonotdistrub() {
        return donotdistrub;
    }

    public void setDonotdistrub(Boolean donotdistrub) {
        this.donotdistrub = donotdistrub;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getMediaVolume() { return  mediaVolume; }

    public void setMediaVolume(Integer mediaVolume) { this.mediaVolume = mediaVolume; }

    public Location(@NonNull Integer id, Double lat, Double lng, Boolean bluethooth, Boolean donotdistrub, String label, Integer mediaVolume) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.bluethooth = bluethooth;
        this.donotdistrub = donotdistrub;
        this.label = label;
        this.mediaVolume = mediaVolume;
    }
    public Location() {

    }
//
//    @NonNull
//    public String getWord() {
//        return this.mWord;
//    }
}
