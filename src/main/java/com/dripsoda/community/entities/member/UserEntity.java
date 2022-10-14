package com.dripsoda.community.entities.member;

import java.util.Date;
import java.util.Objects;

public class UserEntity {
    public static final String ATTRIBUTE_NAME = "memberUser";
    public static final String ATTRIBUTE_NAME_PLURAL = "memberUsers";

    public static UserEntity build() {
        return new UserEntity();
    }

    private String email;
    private String password;
    private String name;
    private String contactCountryValue;
    private String contact;
    private Date policyTermsAt = new Date();
    private Date policyPrivacyAt = new Date();
    private Date policyMarketingAt;
    private String statusValue;
    private Date registeredAt = new Date();
    private boolean isAdmin = false;

    public UserEntity() {
    }

    public UserEntity(String email, String password, String name, String contactCountryValue, String contact, Date policyTermsAt, Date policyPrivacyAt, Date policyMarketingAt, String statusValue, Date registeredAt, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contactCountryValue = contactCountryValue;
        this.contact = contact;
        this.policyTermsAt = policyTermsAt;
        this.policyPrivacyAt = policyPrivacyAt;
        this.policyMarketingAt = policyMarketingAt;
        this.statusValue = statusValue;
        this.registeredAt = registeredAt;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return this.email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public UserEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getContactCountryValue() {
        return this.contactCountryValue;
    }

    public UserEntity setContactCountryValue(String contactCountryValue) {
        this.contactCountryValue = contactCountryValue;
        return this;
    }

    public String getContact() {
        return this.contact;
    }

    public UserEntity setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public Date getPolicyTermsAt() {
        return this.policyTermsAt;
    }

    public UserEntity setPolicyTermsAt(Date policyTermsAt) {
        this.policyTermsAt = policyTermsAt;
        return this;
    }

    public Date getPolicyPrivacyAt() {
        return this.policyPrivacyAt;
    }

    public UserEntity setPolicyPrivacyAt(Date policyPrivacyAt) {
        this.policyPrivacyAt = policyPrivacyAt;
        return this;
    }

    public Date getPolicyMarketingAt() {
        return this.policyMarketingAt;
    }

    public UserEntity setPolicyMarketingAt(Date policyMarketingAt) {
        this.policyMarketingAt = policyMarketingAt;
        return this;
    }

    public String getStatusValue() {
        return this.statusValue;
    }

    public UserEntity setStatusValue(String statusValue) {
        this.statusValue = statusValue;
        return this;
    }

    public Date getRegisteredAt() {
        return this.registeredAt;
    }

    public UserEntity setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public UserEntity setAdmin(boolean admin) {
        isAdmin = admin;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}