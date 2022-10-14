package com.dripsoda.community.controllers;

import com.dripsoda.community.dtos.accompany.ArticleSearchDto;
import com.dripsoda.community.entities.accompany.*;
import com.dripsoda.community.entities.member.UserEntity;
import com.dripsoda.community.enums.CommonResult;
import com.dripsoda.community.interfaces.IResult;
import com.dripsoda.community.services.AccompanyService;
import com.dripsoda.community.services.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller(value = "com.dripsoda.community.controllers.AccompanyController")
@RequestMapping(value = "/accompany")
public class AccompanyController {
    private final AccompanyService accompanyService;
    private final MemberService memberService;

    @Autowired
    public AccompanyController(AccompanyService accompanyService, MemberService memberService) {
        this.accompanyService = accompanyService;
        this.memberService = memberService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getIndex(ModelAndView modelAndView) {
        modelAndView.setViewName("accompany/index");
        return modelAndView;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(@RequestParam(value = "lastArticleId") int lastArticleId,
                            RegionEntity region) throws
            JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject responseJson = new JSONObject();
        ArticleSearchDto[] articles = this.accompanyService.searchArticles(region, lastArticleId);
        for (ArticleSearchDto article : articles) {
            article.setContent(article.getContent()
                    .replaceAll("<[^>]*>", "")
                    .replaceAll("&[^;]*;", ""));
        }
        responseJson.put(ArticleEntity.ATTRIBUTE_NAME_PLURAL, new JSONArray(objectMapper.writeValueAsString(articles)));
        return responseJson.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchIndex() throws
            JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JSONArray continentsJson = new JSONArray(mapper.writeValueAsString(this.accompanyService.getContinents()));
        JSONArray countriesJson = new JSONArray(mapper.writeValueAsString(this.accompanyService.getCountries()));
        JSONArray regionsJson = new JSONArray(mapper.writeValueAsString(this.accompanyService.getRegions()));
        JSONObject responseJson = new JSONObject();
        responseJson.put(ContinentEntity.ATTRIBUTE_NAME_PLURAL, continentsJson);
        responseJson.put(CountryEntity.ATTRIBUTE_NAME_PLURAL, countriesJson);
        responseJson.put(RegionEntity.ATTRIBUTE_NAME_PLURAL, regionsJson);
        return responseJson.toString();
    }

    @RequestMapping(value = "cover-image/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getCoverImage(@PathVariable(value = "id") int id) {
        ArticleEntity article = this.accompanyService.getArticle(id);
        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String[] mimeArray = article.getCoverImageMime().split("/"); // "image/png"
        String mimeType = mimeArray[0]; // "image"
        String mimeSubType = mimeArray[1]; // "png"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(article.getCoverImage().length);
        headers.setContentType(new MediaType(mimeType, mimeSubType, StandardCharsets.UTF_8));
        return new ResponseEntity<>(article.getCoverImage(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "write", method = RequestMethod.GET)
    public ModelAndView getWrite(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                 ModelAndView modelAndView) {
        if (user == null) {
            modelAndView.setViewName("redirect:/member/userLogin");
            return modelAndView;
        }
        modelAndView.setViewName("accompany/write");
        return modelAndView;
    }

    @RequestMapping(value = "write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME) UserEntity user,
                            @RequestParam(value = "coverImageFile") MultipartFile coverImageFile,
                            @RequestParam(value = "dateFromStr") String dateFromStr,
                            @RequestParam(value = "dateToStr") String dateToStr,
                            ArticleEntity article) throws
            IOException,
            ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        article.setIndex(-1)
                .setUserEmail(user.getEmail())
                .setCreatedAt(new Date())
                .setCoverImage(coverImageFile.getBytes())
                .setCoverImageMime(coverImageFile.getContentType())
                .setDateFrom(dateFormat.parse(dateFromStr))
                .setDateTo(dateFormat.parse(dateToStr));
        IResult result = this.accompanyService.putArticle(article);
        JSONObject responseJson = new JSONObject();
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseJson.put("id", article.getIndex());
        }
        return responseJson.toString();
    }

    @RequestMapping(value = "image/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable(value = "id") int id) {
        ImageEntity image = this.accompanyService.getImage(id);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String[] mimeArray = image.getMime().split("/"); // "image/png"
        String mimeType = mimeArray[0]; // "image"
        String mimeSubType = mimeArray[1]; // "png"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(image.getData().length);
        headers.setContentType(new MediaType(mimeType, mimeSubType, StandardCharsets.UTF_8));
        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postImage(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME) UserEntity user,
                            @RequestParam(value = "upload") MultipartFile upload) throws
            IOException {
        ImageEntity image = ImageEntity.build()
                .setUserEmail(user.getEmail())
                .setCreatedAt(new Date())
                .setName(upload.getOriginalFilename())
                .setMime(upload.getContentType())
                .setData(upload.getBytes());
        IResult result = this.accompanyService.uploadImage(image);
        JSONObject responseJson = new JSONObject();
        if (result == CommonResult.SUCCESS) {
            responseJson.put("url", String.format("http://localhost:8080/accompany/image/%d", image.getIndex()));
        } else {
            JSONObject errorJson = new JSONObject();
            errorJson.put("message", "이미지 업로드에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
            responseJson.put("error", errorJson);
        }
        return responseJson.toString();
    }

    @RequestMapping(value = "read/{id}", method = RequestMethod.GET)
    public ModelAndView getRead(@PathVariable(value = "id") int id,
                                ModelAndView modelAndView) {
        modelAndView.setViewName("accompany/read");
        return modelAndView;
    }

    @RequestMapping(value = "read/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRead(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false)
                           UserEntity user,
                           @PathVariable(value = "id") int id,
                           HttpServletResponse response) throws
            JsonProcessingException {
        ArticleEntity article = this.accompanyService.getArticle(id);
        if (article == null) {
            response.setStatus(404);
            return null;
        }
        article.setCoverImage(null)
                .setCoverImageMime(null);
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject responseJson = new JSONObject(objectMapper.writeValueAsString(article));
        UserEntity articleUser = this.memberService.getUser(article.getUserEmail());
        responseJson.put("userName", articleUser.getName());
        responseJson.put("mine", user != null && (user.isAdmin() || user.equals(articleUser)));
        return responseJson.toString();
    }

    @RequestMapping(value = "read/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteRead(
            @SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
            @PathVariable(value = "id") int id) {
        JSONObject responseJson = new JSONObject();
        ArticleEntity article = this.accompanyService.getArticle(id);
        if (article == null) {
            responseJson.put(IResult.ATTRIBUTE_NAME, CommonResult.FAILURE);
            return responseJson.toString();
        }
        if (user == null || !user.isAdmin() && !user.getEmail().equals(article.getUserEmail())) {
            responseJson.put(IResult.ATTRIBUTE_NAME, "k");
            return responseJson.toString();
        }
        IResult result = this.accompanyService.deleteArticle(id);
        responseJson.put(IResult.ATTRIBUTE_NAME, result.name().toLowerCase());
        return responseJson.toString();
    }

    @RequestMapping(value = "modify/{id}", method = RequestMethod.GET)
    public ModelAndView getWrite(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                                 @PathVariable(value = "id") int id,
                                 ModelAndView modelAndView) {
        if (user == null) {
            modelAndView.setViewName("redirect:/member/userLogin");
            return modelAndView;
        }
        modelAndView.setViewName("accompany/modify");
        return modelAndView;
    }

    @RequestMapping(value = "modify/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModify(@SessionAttribute(value = UserEntity.ATTRIBUTE_NAME, required = false) UserEntity user,
                              @PathVariable(value = "id") int id,
                              HttpServletResponse response) throws JsonProcessingException {
        ArticleEntity article = this.accompanyService.getArticle(id);
        if (article == null) {
            response.setStatus(404);
            return null;
        }
        if (user == null || !user.isAdmin() && !user.getEmail().equals(article.getUserEmail())) {
            response.setStatus(403);
            return null;
        }
        article.setCoverImage(null)
                .setCoverImageMime(null);
        return new ObjectMapper().writeValueAsString(article);
    }
}
















