package org.myplaylist.myplaylist.config.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.view.CommentViewModel;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentEntity commentBindingModelToEntity(CommentBindingModel commentBindingModel);
    @Mapping(source = "author.username", target = "authorName")
    CommentViewModel commentEntityToViewModel(CommentEntity commentEntity);

}
