package com.dripsoda.community.entities.member;

import java.util.Objects;

public class ContactCountryEntity {
    public static final String ATTRIBUTE_NAME = "memberContactCountry";
    public static final String ATTRIBUTE_NAME_PLURAL = "memberContactCountries";

    public static ContactCountryEntity build() {
        return new ContactCountryEntity();
    }

    private String value;
    private String text;

    public ContactCountryEntity() {
    }

    public ContactCountryEntity(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return this.value;
    }

    public ContactCountryEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public ContactCountryEntity setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactCountryEntity that = (ContactCountryEntity) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}