package com.athena.service.copy;

import com.athena.exception.IdOfResourceNotFoundException;
import com.athena.model.Copy;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by 吴钟扬 on 2017/9/12.
 * <p>
 * Copy operation for BookCopy,JournalCopy...
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 * @param <FK> the type parameter
 */
interface CopyService<T extends Copy, ID extends Serializable, FK extends Serializable> extends GenericCopyService<T, ID> {

    /**
     * Gets copies.
     *
     * @param fkList the fk list
     * @return the copies
     * @throws IdOfResourceNotFoundException the id of resource not found exception
     */
    List<T> getCopies(FK fkList) throws IdOfResourceNotFoundException;

    /**
     * Delete copies.
     *
     * @param fk the fk
     * @throws IdOfResourceNotFoundException the id of resource not found exception
     */
    void deleteCopies(FK fk) throws IdOfResourceNotFoundException;

    /**
     * Check if the copy of fk is not fit the predicate
     *
     * @param fk        the fk
     * @param predicate the predicate
     * @return the list
     * @throws IdOfResourceNotFoundException the id of resource not found exception
     */
    default List<T> copyChecker(FK fk, Predicate<? super T> predicate) throws IdOfResourceNotFoundException {
        return this.getCopies(fk).stream().filter(predicate).collect(Collectors.toList());
        //todo: test
    }

}