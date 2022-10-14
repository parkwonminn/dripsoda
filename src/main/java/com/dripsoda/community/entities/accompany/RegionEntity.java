package com.dripsoda.community.entities.accompany;

import java.util.Objects;

public class RegionEntity {
    public static final String ATTRIBUTE_NAME = "accompanyRegion";
    public static final String ATTRIBUTE_NAME_PLURAL = "accompanyRegions";

    public static RegionEntity build() {
        return new RegionEntity();
    }

    private String continentValue;
    private String countryValue;
    private String value;
    private String text;

    public RegionEntity() {
    }

    public RegionEntity(String continentValue, String countryValue, String value, String text) {
        this.continentValue = continentValue;
        this.countryValue = countryValue;
        this.value = value;
        this.text = text;
    }

    public String getContinentValue() {
        return this.continentValue;
    }

    public RegionEntity setContinentValue(String continentValue) {
        this.continentValue = continentValue;
        return this;
    }

    public String getCountryValue() {
        return this.countryValue;
    }

    public RegionEntity setCountryValue(String countryValue) {
        this.countryValue = countryValue;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public RegionEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public RegionEntity setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionEntity that = (RegionEntity) o;
        return Objects.equals(continentValue, that.continentValue) && Objects.equals(countryValue, that.countryValue) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(continentValue, countryValue, value);
    }
}