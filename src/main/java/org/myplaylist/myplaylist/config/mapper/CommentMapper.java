package org.myplaylist.myplaylist.config.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.myplaylist.myplaylist.model.entity.CommentEntity;
import org.myplaylist.myplaylist.model.view.CommentViewModel;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "user", target = "user")
    CommentViewModel commentEntityToViewModel(CommentEntity commentEntity);
}
