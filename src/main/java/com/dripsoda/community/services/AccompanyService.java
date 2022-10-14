package com.dripsoda.community.services;

import com.dripsoda.community.dtos.accompany.ArticleSearchDto;
import com.dripsoda.community.entities.accompany.*;
import com.dripsoda.community.entities.member.UserEntity;
import com.dripsoda.community.enums.CommonResult;
import com.dripsoda.community.enums.accompany.RequestResult;
import com.dripsoda.community.interfaces.IResult;
import com.dripsoda.community.mappers.IAccompanyMapper;
import com.dripsoda.community.mappers.IMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "com.dripsoda.community.services.AccompanyService")
public class AccompanyService {
    private final IAccompanyMapper accompanyMapper;
    private final IMemberMapper memberMapper;

    @Autowired
    public AccompanyService(IAccompanyMapper accompanyMapper, IMemberMapper memberMapper) {
        this.accompanyMapper = accompanyMapper;
        this.memberMapper = memberMapper;
    }

    public ContinentEntity[] getContinents() {
        return this.accompanyMapper.selectContinents();
    }

    public CountryEntity[] getCountries() {
        return this.accompanyMapper.selectCountries();
    }

    public RegionEntity[] getRegions() {
        return this.accompanyMapper.selectRegions();
    }

    public ImageEntity getImage(int index) {
        return this.accompanyMapper.selectImageByIndex(index);
    }

    public IResult uploadImage(ImageEntity image) {
        return this.accompanyMapper.insertImage(image) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ArticleEntity getArticle(int index) {
        return this.accompanyMapper.selectArticleByIndex(index);
    }

    public ArticleSearchDto[] searchArticles(RegionEntity region, int lastArticleIndex) {
        return this.accompanyMapper.selectArticlesForSearch(region, lastArticleIndex);
    }

    public IResult putArticle(ArticleEntity article) {
        return this.accompanyMapper.insertArticle(article) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public IResult deleteArticle(int index) {
        return this.accompanyMapper.deleteArticle(index) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public IResult modifyArticle(ArticleEntity article) {
        ArticleEntity oldArticle = this.accompanyMapper.selectArticleByIndex(article.getIndex());
        if (oldArticle == null) {
            return CommonResult.FAILURE;
        }
        if (!article.getUserEmail().equals(oldArticle.getUserEmail())) {
            return CommonResult.FAILURE;
        }
        if (article.getCoverImage() == null) {
            article.setCoverImage(oldArticle.getCoverImage())
                    .setCoverImageMime(oldArticle.getCoverImageMime());
        }
        article.setIndex(oldArticle.getIndex())
                .setUserEmail(oldArticle.getUserEmail())
                .setCreatedAt(oldArticle.getCreatedAt());
        return this.accompanyMapper.updateArticle(article) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public IResult putRequest(UserEntity requester, int articleIndex) {
        if (requester == null) {
            return RequestResult.NOT_SIGNED;
        }
        ArticleEntity article = this.accompanyMapper.selectArticleByIndex(articleIndex);
        if (article == null) {
            return RequestResult.NOT_Found;
        }
        UserEntity requestee = this.memberMapper.selectUserByEmail(UserEntity.build().setEmail(article.getUserEmail()));
        if (requester.equals(requestee)) {
            return RequestResult.YOURSELF;
        }
        RequestEntity request = RequestEntity.build()
                .setRequesterUserEmail(requester.getEmail())
                .setRequesteeUserEmail(requestee.getEmail())
                .setArticleIndex(articleIndex)
                .setCreatedAt(new Date())
                .setGranted(false)
                .setDeclined(false);
        return this.accompanyMapper.insertRequest(request) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public boolean  checkRequest(UserEntity requester, int articleIndex) {
        if (requester == null) {
            return false;
        }
        return this.accompanyMapper.selectRequestByRequesterArticleIndex(requester.getEmail(), articleIndex) != null;
    }
}