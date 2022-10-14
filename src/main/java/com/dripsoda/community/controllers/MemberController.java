package com.dripsoda.community.controllers;

import com.dripsoda.community.entities.member.ContactAuthEntity;
import com.dripsoda.community.entities.member.ContactCountryEntity;
import com.dripsoda.community.entities.member.EmailAuthEntity;
import com.dripsoda.community.entities.member.UserEntity;
import com.dripsoda.community.enums.CommonResult;
import com.dripsoda.community.exceptions.RollbackException;
import com.dripsoda.community.interfaces.IResult;
import com.dripsoda.community.regex.MemberRegex;
import com.dripsoda.community.services.MemberService;
import com.dripsoda.community.utils.CryptoUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Controller(value = "com.dripsoda.community.controllers.MemberController")
@RequestMapping(value = "/member")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @RequestMapping(value = "userEmailCheck", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getUserEmailCheck(UserEntity user) {
        IResult result = this.memberService.checkUserEmail(user);
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userRecoverEmail", method = RequestMethod.GET)
    public ModelAndView getUserRecoverEmail(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                            ModelAndView modelAndView) {
        if (user != null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        modelAndView.addObject(ContactCountryEntity.ATTRIBUTE_NAME_PLURAL, this.memberService.getContactCountries());
        modelAndView.setViewName("member/userRecoverEmail");
        return modelAndView;
    }

    @RequestMapping(value = "userRecoverEmail", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserRecoverEmail(ContactAuthEntity contactAuth) {
        contactAuth.setIndex(-1)
                .setCreatedAt(null)
                .setExpiresAt(null)
                .setExpired(false);
        UserEntity user = UserEntity.build();
        IResult result = this.memberService.findUserEmail(contactAuth, user);
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseJson.put("email", user.getEmail());
        }
        return responseJson.toString();
    }

    @RequestMapping(value = "userRecoverEmailAuth", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getUserRecoverEmailAuth(UserEntity user) throws
            IOException,
            InvalidKeyException,
            NoSuchAlgorithmException {
        user.setEmail(null)
                .setPassword(null)
                .setPolicyTermsAt(null)
                .setPolicyPrivacyAt(null)
                .setPolicyMarketingAt(null)
                .setStatusValue(null)
                .setRegisteredAt(null)
                .setAdmin(false);
        IResult result;
        ContactAuthEntity contactAuth = ContactAuthEntity.build();
        try {
            result = this.memberService.recoverUserEmailAuth(user, contactAuth);
        } catch (RollbackException ex) {
            result = CommonResult.FAILURE;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseJson.put("salt", contactAuth.getSalt());
        }
        return responseJson.toString();
    }

    @RequestMapping(value = "userRecoverEmailAuth", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserRecoverEmailAuth(ContactAuthEntity contactAuth) {
        return this.postUserRegisterAuth(contactAuth); // TODO : Contact authentication of register and email recovery is merged.
    }

    @RequestMapping(value = "userRecoverPassword", method = RequestMethod.GET)
    public ModelAndView getUserRecoverPassword(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                               ModelAndView modelAndView) {
        if (user != null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        modelAndView.setViewName("member/userRecoverPassword");
        return modelAndView;
    }

    @RequestMapping(value = "userRecoverPassword", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserRecoverPassword(UserEntity user) throws
            MessagingException,
            RollbackException {
        user.setPassword(null)
                .setName(null)
                .setContactCountryValue(null)
                .setContact(null)
                .setPolicyTermsAt(null)
                .setPolicyPrivacyAt(null)
                .setPolicyMarketingAt(null)
                .setStatusValue(null)
                .setRegisteredAt(null)
                .setAdmin(false);
        IResult result = this.memberService.recoverUserPassword(user);
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userResetPassword", method = RequestMethod.GET)
    public ModelAndView getUserResetPassword(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                             ModelAndView modelAndView) {
        if (user != null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        modelAndView.setViewName("member/userResetPassword");
        return modelAndView;
    }

    @RequestMapping(value = "userResetPassword", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserResetPassword(EmailAuthEntity emailAuth,
                                        UserEntity user) {
        emailAuth.setEmail(null)
                .setCreatedAt(null)
                .setExpiresAt(null)
                .setExpired(false);
        user.setEmail(null)
                .setName(null)
                .setContactCountryValue(null)
                .setContact(null)
                .setPolicyTermsAt(null)
                .setPolicyPrivacyAt(null)
                .setPolicyMarketingAt(null)
                .setStatusValue(null)
                .setRegisteredAt(null)
                .setAdmin(false);
        IResult result;
        try {
            result = this.memberService.resetPassword(emailAuth, user);
        } catch (RollbackException ex) {
            result = ex.result;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userLogin", method = RequestMethod.GET)
    public ModelAndView getUserLogin(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                     ModelAndView modelAndView) {
        if (user != null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        modelAndView.setViewName("member/userLogin");
        return modelAndView;
    }

    @RequestMapping(value = "userLogin", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserLogin(@RequestParam(value = "autosign", required = false) Optional<Boolean> autosignOptional,
                                HttpSession session,
                                UserEntity user) {
        boolean autosign = autosignOptional.orElse(false);
        user.setName(null)
                .setContactCountryValue(null)
                .setContact(null)
                .setPolicyTermsAt(null)
                .setPolicyPrivacyAt(null)
                .setPolicyMarketingAt(null)
                .setStatusValue(null)
                .setRegisteredAt(null)
                .setAdmin(false);
        IResult result = this.memberService.loginUser(user);
        if (result == CommonResult.SUCCESS) {
            session.setAttribute(UserEntity.ATTRIBUTE_NAME, user);
            if (autosign) {
                // TODO : Implement auto sign feature.
            }
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userRegister", method = RequestMethod.GET)
    public ModelAndView getUserRegister(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                        ModelAndView modelAndView) {
        if (user != null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        modelAndView.addObject(ContactCountryEntity.ATTRIBUTE_NAME_PLURAL, this.memberService.getContactCountries());
        modelAndView.setViewName("member/userRegister");
        return modelAndView;
    }

    @RequestMapping(value = "userRegister", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserRegister(@RequestParam(value = "policyMarketing", required = true) boolean policyMarketing,
                                   ContactAuthEntity contactAuth,
                                   UserEntity user) {
        contactAuth.setIndex(-1)
                .setCreatedAt(null)
                .setExpiresAt(null)
                .setExpired(false);
        user.setPolicyTermsAt(new Date())
                .setPolicyPrivacyAt(new Date())
                .setPolicyMarketingAt(policyMarketing ? new Date() : null)
                .setStatusValue("OKY")
                .setRegisteredAt(new Date())
                .setAdmin(false);
        IResult result;
        try {
            result = this.memberService.createUser(contactAuth, user);
        } catch (RollbackException ex) {
            result = ex.result;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userRegisterDone", method = RequestMethod.GET)
    public ModelAndView getUserRegisterDone(ModelAndView modelAndView) {
        modelAndView.setViewName("member/userRegisterDone");
        return modelAndView;
    }

    @RequestMapping(value = "userRegisterAuth", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getUserRegisterAuth(ContactAuthEntity contactAuth) throws
            InvalidKeyException,
            IOException,
            NoSuchAlgorithmException {
        contactAuth.setIndex(-1)
                .setCode(null)
                .setSalt(null)
                .setCreatedAt(null)
                .setExpiresAt(null)
                .setExpired(false);
        IResult result;
        try {
            result = this.memberService.registerAuth(contactAuth);
        } catch (RollbackException ex) {
            result = ex.result;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseJson.put("salt", contactAuth.getSalt());
        }
        return responseJson.toString();
    }

    @RequestMapping(value = "userRegisterAuth", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserRegisterAuth(ContactAuthEntity contactAuth) {
        contactAuth.setIndex(-1)
                .setCreatedAt(null)
                .setExpiresAt(null)
                .setExpired(false);
        IResult result;
        try {
            result = this.memberService.checkContactAuth(contactAuth);
        } catch (RollbackException ex) {
            result = ex.result;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userLogout", method = RequestMethod.GET)
    public ModelAndView getUserLogout(ModelAndView modelAndView,
                                      HttpSession session) {
        session.removeAttribute(UserEntity.ATTRIBUTE_NAME);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @RequestMapping(value = "userMy", method = RequestMethod.GET)
    public ModelAndView getUserMy(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                  @RequestParam(value = "tab", required = false, defaultValue = "info") String tab,
                                  ModelAndView modelAndView) {
        if (user == null) {
            modelAndView.setViewName("redirect:/member/userLogin");
            return modelAndView;
        }
        if (tab == null || tab.equals("info") || (!tab.equals("trip") && !tab.equals("book") && !tab.equals("comment") && !tab.equals("accompany") && !tab.equals("truncate"))) {
            modelAndView.addObject(ContactCountryEntity.ATTRIBUTE_NAME_PLURAL, this.memberService.getContactCountries());
        }
        modelAndView.setViewName("member/userMy");
        return modelAndView;
    }

    @RequestMapping(value = "userMyInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUserMyInfo(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity currentUser,
                                 @RequestParam(value = "oldPassword") String oldPassword,
                                 @RequestParam(value = "changePassword") Optional<Boolean> changePasswordOptional,
                                 @RequestParam(value = "changeContact") Optional<Boolean> changeContactOptional,
                                 @RequestParam(value = "newPassword", required = false, defaultValue = "") String newPassword,
                                 @RequestParam(value = "newContact", required = false, defaultValue = "") String newContact,
                                 @RequestParam(value = "newContactAuthCode", required = false, defaultValue = "") String newContactAuthCode,
                                 @RequestParam(value = "newContactAuthSalt", required = false, defaultValue = "") String newContactAuthSalt) {
        UserEntity newUser = UserEntity.build();
        if (changePasswordOptional.orElse(false)) {
            newUser.setPassword(newPassword);
        }
        if (changeContactOptional.orElse(false)) {
            newUser.setContact(newContact);
        }
        ContactAuthEntity contactAuth = ContactAuthEntity.build()
                .setContact(newContact)
                .setCode(newContactAuthCode)
                .setSalt(newContactAuthSalt);
        IResult result;
        try {
            result = this.memberService.modifyUser(currentUser, newUser, oldPassword, contactAuth);
        } catch (RollbackException ex) {
            result = ex.result;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "userMyInfoAuth", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getUserMyInfoAuth(@RequestParam(value = "newContact") String newContact) throws
            InvalidKeyException,
            IOException,
            NoSuchAlgorithmException {
        ContactAuthEntity contactAuth = ContactAuthEntity.build().setContact(newContact);
        IResult result;
        try {
            result = this.memberService.modifyUserContactAuth(contactAuth);
        } catch (RollbackException ex) {
            result = ex.result;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseJson.put("salt", contactAuth.getSalt());
        }
        return responseJson.toString();
    }

    @RequestMapping(value = "userMyInfoAuth", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String postUserMyInfoAuth(@RequestParam(value = "newContact") String newContact,
                                     @RequestParam(value = "newContactAuthCode") String newContactAuthCode,
                                     @RequestParam(value = "newContactAuthSalt") String newContactAuthSalt) throws
            RollbackException {
        ContactAuthEntity contactAuth = ContactAuthEntity.build()
                .setContact(newContact)
                .setCode(newContactAuthCode)
                .setSalt(newContactAuthSalt);
        IResult result = this.memberService.checkContactAuth(contactAuth);
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }
}












