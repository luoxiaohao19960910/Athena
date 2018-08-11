package com.athena.service.security;

import com.athena.annotation.ArgumentNotNull;
import com.athena.exception.http.ResourceNotDeletable;
import com.athena.exception.http.ResourceNotFoundByIdException;
import com.athena.model.security.BlockRecord;
import com.athena.model.security.NewUserVo;
import com.athena.model.security.User;
import com.athena.repository.jpa.BlockRecordRepository;
import com.athena.repository.jpa.UserRepository;
import com.athena.service.ModelCRUDService;
import com.athena.util.EntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tommy on 2017/3/20.
 */
@Service
public class UserService implements ModelCRUDService<User, Long> {

    private UserRepository userRepository;
    private BlockRecordRepository blockRecordRepository;

    /**
     * Instantiates a new User service.
     *
     * @param repository the userRepository
     */
    @Autowired
    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    /**
     * Gets user.
     *
     * @param id the id
     * @return the user
     */
    public User get(Long id) {
        return userRepository.findOne(id);
    }

    public List<User> get(Iterable<Long> ids) {
        return userRepository.findAll(ids);
    }

    @Override
    public User update(User user) throws ResourceNotFoundByIdException {
        EntityUtil.requireEntityNotNull(user);
        if (!this.userRepository.exists(user.getId())) {
            throw new ResourceNotFoundByIdException();
        }
        return userRepository.save(user);
    }

    @Override
    @ArgumentNotNull
    public void delete(User user) throws ResourceNotFoundByIdException, ResourceNotDeletable {
        userRepository.delete(user);
    }

    @Override
    @ArgumentNotNull
    public void delete(Iterable<User> users) throws ResourceNotFoundByIdException, ResourceNotDeletable {
        userRepository.delete(users);
    }

    /**
     * Gets user.
     *
     * @param id the id
     * @return the user
     */
    public User get(int id) {
        return userRepository.findOne(Long.valueOf(id));
    }

    /**
     * Save.
     *
     * @param user the user
     */
    public User add(User user) {
        return userRepository.save(user);
    }

    public User add(NewUserVo newUser){
        return this.add(new User(newUser));//todo: validate privilege
    }


    /**
     * Block User by creating a block record
     *
     * @param blockedUser
     * @param handler
     * @return
     */
    public BlockRecord blockUser(User blockedUser, User handler) {
        return this.blockUser(blockedUser, handler, null);
    }

    public BlockRecord blockUser(User blockedUser, User handler, String note) {
        BlockRecord blockRecord = new BlockRecord();
        blockRecord.setBlockedUser(blockedUser);
        blockRecord.setBlockHandler(handler);
        blockRecord.setNote(note);
        this.blockRecordRepository.save(blockRecord);
        return blockRecord;
    }

}
