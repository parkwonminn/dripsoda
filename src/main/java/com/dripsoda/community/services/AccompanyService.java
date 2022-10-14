package com.dripsoda.community.services;

import com.dripsoda.community.dtos.accompany.ArticleSearchDto;
import com.dripsoda.community.entities.accompany.*;
import com.dripsoda.community.entities.member.UserEntity;
import com.dripsoda.community.enums.CommonResult;
import com.dripsoda.community.interfaces.IResult;
import com.dripsoda.community.mappers.IAccompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "com.dripsoda.community.services.AccompanyService")
public class AccompanyService {
    private final IAccompanyMapper accompanyMapper;

    @Autowired
    public AccompanyService(IAccompanyMapper accompanyMapper) {
        this.accompanyMapper = accompanyMapper;
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

}