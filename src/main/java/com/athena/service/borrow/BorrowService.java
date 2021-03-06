package com.athena.service.borrow;

import com.athena.annotation.ArgumentNotNull;
import com.athena.exception.http.IllegalBorrowRequest;
import com.athena.exception.http.IllegalReturnRequest;
import com.athena.exception.http.ResourceNotDeletable;
import com.athena.exception.http.ResourceNotFoundByIdException;
import com.athena.model.borrow.Borrow;
import com.athena.model.copy.CopyStatus;
import com.athena.model.copy.SimpleCopy;
import com.athena.model.security.Account;
import com.athena.repository.jpa.BorrowRepository;
import com.athena.repository.jpa.copy.SimpleCopyRepository;
import com.athena.service.ModelCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Tommy on 2017/11/5.
 */
@Service
public class BorrowService implements ModelCRUDService<Borrow, String> {

    private SimpleCopyRepository simpleCopyRepository;
    private BorrowRepository borrowRepository;
    private BorrowVerificationService borrowVerificationService;


    /**
     * Instantiates a new Borrow service.
     *
     * @param borrowRepository          the borrow repository
     * @param borrowVerificationService the borrow verification service
     * @param simpleCopyRepository      the simple copy repository
     */
    @Autowired
    public BorrowService(BorrowRepository borrowRepository, BorrowVerificationService borrowVerificationService, SimpleCopyRepository simpleCopyRepository) {
        this.borrowRepository = borrowRepository;
        this.borrowVerificationService = borrowVerificationService;
        this.simpleCopyRepository = simpleCopyRepository;
    }

    @Override
    @Transactional
    public Borrow add(Borrow borrow) {
        //set status of copy
        SimpleCopy copy = borrow.getCopy();
        copy.setStatus(CopyStatus.CHECKED_OUT);
        borrow.setCopy(this.simpleCopyRepository.save(copy));
        return this.borrowRepository.save(borrow);
    }

    @Override
    @ArgumentNotNull
    public Borrow get(String id) throws ResourceNotFoundByIdException {
        Borrow borrow = this.borrowRepository.findOne(id);
        if (borrow == null) {
            throw new ResourceNotFoundByIdException();
        }
        return borrow;
    }

    @Override
    public Borrow update(Borrow borrow) throws ResourceNotFoundByIdException {
        if (!this.borrowRepository.exists(borrow.getId())) {
            throw new ResourceNotFoundByIdException();
        }
        this.borrowRepository.save(borrow);
        return borrow;
    }

    @Override
    public void delete(Borrow borrow) throws ResourceNotFoundByIdException, ResourceNotDeletable {
        this.borrowRepository.delete(borrow);
    }

    /**
     * Borrow one copy.
     *
     * @param account the account
     * @param copy    the copy
     * @return the borrow instance
     * @throws IllegalBorrowRequest the illegal borrow request. Because user is blocked and copy can not be borrowed
     */
    public Borrow borrowCopy(Account account, SimpleCopy copy) throws IllegalBorrowRequest {
        Borrow borrow = new Borrow();
        borrow.setUser(account.getUser());
        borrow.setCopy(copy);

        if (borrowVerificationService.userCanBorrow(account.getUser()) && borrowVerificationService.copyCanBorrow(copy)) {
            return this.add(borrow);
        }
        throw new IllegalBorrowRequest();
    }

    /**
     * Return copy borrow.
     *
     * @param id      the id
     * @param account the account
     * @return the borrow
     * @throws IllegalReturnRequest the illegal return request
     */
    public Borrow returnCopy(String id, Account account) throws IllegalReturnRequest {
        return this.returnCopy(id, account, false);
    }

    /**
     * Return copy borrow.
     *
     * @param id            the id
     * @param account       the account
     * @param isSelfService the is self service
     * @return the borrow
     * @throws IllegalReturnRequest the illegal return request
     */
    @Transactional
    @ArgumentNotNull
    public Borrow returnCopy(String id, Account account, Boolean isSelfService) throws IllegalReturnRequest {
        //check if the account has the borrow
        Borrow borrow = this.borrowRepository.findOne(id);
        if (borrow == null) {
            //if the borrow doesn't exist
            throw new IllegalReturnRequest();
        }
        if (borrow.getUser() != account.getUser()) {
            //if the user does not has the borrow
            throw new IllegalReturnRequest();
        }
        return this.returnCopy(borrow, isSelfService);
    }

    private Borrow returnCopy(Borrow borrow, Boolean isSelfService) throws IllegalReturnRequest {
        //check status
        if (borrowVerificationService.canReturn(borrow)) {
            return this.setReturnStatus(borrow, isSelfService);
        }
        throw new IllegalReturnRequest();
    }

    private Borrow setReturnStatus(Borrow borrow, boolean isSelfService) {
        borrow.setEnable(false);
        if (isSelfService) {
            borrow.getCopy().setStatus(CopyStatus.WAIT_FOR_VERIFY);
        } else {
            borrow.getCopy().setStatus(CopyStatus.AVAILABLE);
        }
        borrow.setCopy(this.simpleCopyRepository.save(borrow.getCopy()));
        return borrow;
    }

}
