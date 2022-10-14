package com.dripsoda.community.mappers;

import com.dripsoda.community.entities.member.ContactAuthEntity;
import com.dripsoda.community.entities.member.ContactCountryEntity;
import com.dripsoda.community.entities.member.EmailAuthEntity;
import com.dripsoda.community.entities.member.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMemberMapper {
    int insertContactAuth(ContactAuthEntity contactAuth);

    int insertEmailAuth(EmailAuthEntity emailAuth);

    int insertUser(UserEntity user);

    ContactAuthEntity selectContactAuthByContactCodeSalt(ContactAuthEntity contactAuth);

    ContactCountryEntity[] selectContactCountries();

    EmailAuthEntity selectEmailAuthByIndex(EmailAuthEntity emailAuth);

    UserEntity selectUserByEmail(UserEntity user);

    UserEntity selectUserByNameContact(UserEntity user);

    UserEntity selectUserByEmailPassword(UserEntity user);

    UserEntity selectUserByContact(UserEntity user);

    int updateContactAuth(ContactAuthEntity contactAuth);

    int updateEmailAuth(EmailAuthEntity emailAuth);

    int updateUser(UserEntity user);
}