package org.myplaylist.myplaylist.config;

import org.mapstruct.Mapper;
import org.myplaylist.myplaylist.model.binding.UserLoginBindingModel;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.model.view.UserViewModel;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRegistrationBindingModel userEntityToUserDTO(UserEntity user);
    UserEntity userDTOtoUserEntity(UserRegistrationBindingModel dto);

    UserLoginBindingModel userEntitytToUserLoginBindingModel(UserEntity user);

    SongViewModel songEntityToViewModel(SongEntity song);

    PlaylistViewModel playlistEntityToViewModel(PlaylistEntity playlistEntity);

    UserViewModel userEntityToViewModel(UserEntity user);
}
