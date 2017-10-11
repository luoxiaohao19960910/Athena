package com.athena.repository.jpa.copy;

import com.athena.model.BookCopy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Tommy on 2017/9/24.
 */
@Component
public class BookCopyRepositoryImpl implements CopyRepositoryCustom<BookCopy, Long> {

    @PersistenceContext
    private EntityManager em;

    @Value("${copy.status.isDeletable}")
    private String deletable;


    @Override
    public void update(BookCopy copy) {
        Query query = em.createNativeQuery("UPDATE book_copy INNER JOIN copy ON copy.id = book_copy.copy_id SET copy_id=?1,isbn=?2,status=?3,updated_date=?4,created_date=?5 WHERE copy_id=?1");
        query.setParameter(1, copy.getId());
        query.setParameter(2, copy.getBook().getIsbn());
        query.setParameter(3, copy.getStatus());
        query.setParameter(4, copy.getUpdatedDate());
        query.setParameter(5, copy.getCreatedDate());
        query.executeUpdate();
    }

    @Override
    public List<BookCopy> isNotDeletable(Long id) {
        Query query = em.createNativeQuery("SELECT * FROM book_copy INNER JOIN copy ON book_copy.copy_id = copy.id WHERE isbn=?1 AND status NOT IN (?2)");
        query.setParameter(1, id);
        query.setParameter(2, deletable);
        return query.getResultList();
    }


}
