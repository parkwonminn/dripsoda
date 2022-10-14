package com.dripsoda.community.mappers;

import com.dripsoda.community.dtos.accompany.ArticleSearchDto;
import com.dripsoda.community.entities.accompany.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IAccompanyMapper {

    int deleteArticle(@Param(value = "index") int index);
    int insertImage(ImageEntity image);

    int insertArticle(ArticleEntity article);

    ContinentEntity[] selectContinents();

    ArticleSearchDto[] selectArticlesForSearch(RegionEntity region, int lastArticleIndex);

    ArticleEntity selectArticleByIndex(@Param(value = "index") int index);

    CountryEntity[] selectCountries();

    RegionEntity[] selectRegions();

    ImageEntity selectImageByIndex(@Param(value = "index") int index);
}