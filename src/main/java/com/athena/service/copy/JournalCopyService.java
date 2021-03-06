package com.athena.service.copy;

import com.athena.exception.http.IllegalEntityAttributeException;
import com.athena.exception.http.MixedCopyTypeException;
import com.athena.exception.http.ResourceNotFoundByIdException;
import com.athena.model.copy.JournalCopy;
import com.athena.model.publication.Journal;
import com.athena.model.publication.JournalPK;
import com.athena.repository.jpa.JournalRepository;
import com.athena.repository.jpa.copy.JournalCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Tommy on 2017/9/2.
 */
@Service
public class JournalCopyService implements CopyService<JournalCopy, JournalPK> {

    private final JournalCopyRepository journalCopyRepository;
    private final JournalRepository journalRepository;

    /**
     * Instantiates a new Journal copy service.
     *
     * @param journalCopyRepository the journal copy repository
     * @param journalRepository     the journal repository
     */
    @Autowired
    public JournalCopyService(JournalCopyRepository journalCopyRepository, JournalRepository journalRepository) {
        this.journalCopyRepository = journalCopyRepository;
        this.journalRepository = journalRepository;
    }

    @Override
    public JournalCopy add(JournalCopy copy) {
        return this.journalCopyRepository.save(copy);
    }

    @Override
    public List<JournalCopy> add(Iterable<JournalCopy> copies) {
        return this.journalCopyRepository.save(copies);
    }

    @Override
    public JournalCopy get(Long id) throws ResourceNotFoundByIdException {
        JournalCopy copy = this.journalCopyRepository.findByIdAndJournalIsNotNull(id);
        if (copy == null) {
            throw new ResourceNotFoundByIdException();
        }
        return copy;
    }

    @Override
    public List<JournalCopy> get(Iterable<Long> idList) {
        return this.journalCopyRepository.findByIdIsInAndJournalIsNotNull(idList);
    }

    /**
     * Get all copies of certain journal
     *
     * @param fkList the key of journal
     * @return
     * @throws ResourceNotFoundByIdException
     */
    @Override
    public List<JournalCopy> getCopies(JournalPK fkList) throws ResourceNotFoundByIdException {
        Journal journal = this.journalRepository.findOne(fkList);
        if (journal == null) {
            throw new ResourceNotFoundByIdException();
        }
        return this.journalCopyRepository.findByJournal(journal);
    }

    @Override
    public void deleteCopies(JournalPK journalPK) throws ResourceNotFoundByIdException {
        this.journalCopyRepository.delete(this.getCopies(journalPK));
    }

    @Override
    public void deleteById(Long id) {
        this.journalCopyRepository.delete(id);
    }

    @Override
    public void deleteById(List<Long> ids) throws MixedCopyTypeException {
        List<JournalCopy> copies = this.get(ids);
        if (ids.size() != copies.size()) {
            throw new MixedCopyTypeException(JournalCopy.class);
        }
        this.journalCopyRepository.delete(copies);
    }

    @Override
    public JournalCopy update(JournalCopy copy) {
        return this.journalCopyRepository.update(copy);
    }

    @Override
    public List<JournalCopy> update(Iterable<JournalCopy> copyList) throws IllegalEntityAttributeException {
        try {
            return this.journalCopyRepository.update(copyList);
        } catch (Exception e) {
            throw new IllegalEntityAttributeException();
        }
    }

}
