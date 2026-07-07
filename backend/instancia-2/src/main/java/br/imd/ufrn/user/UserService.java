package br.imd.ufrn.user;

import br.imd.ufrn.user.dto.CreateUserRequest;
import br.imd.ufrn.user.dto.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    List<UserResponse> findAll();
}
