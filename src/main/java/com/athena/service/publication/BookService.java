package com.athena.service.publication;

import com.athena.exception.http.ResourceNotDeletable;
import com.athena.exception.http.ResourceNotFoundByIdException;
import com.athena.model.copy.BookCopy;
import com.athena.model.publication.Book;
import com.athena.repository.jpa.BookRepository;
import com.athena.repository.jpa.PublisherRepository;
import com.athena.repository.jpa.copy.BookCopyRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by tommy on 2017/3/28.
 */
@Service
public class BookService implements PublicationService<Book, Long> {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookCopyRepository bookCopyRepository;

    @NotNull
    private Page<Book> ListToPage(Pageable pageable, List<Book> list) {
        int start = pageable.getOffset();//Get the start index
        int pageSize = pageable.getPageSize();
        int end = (start + pageSize) > list.size() ? list.size() : (start + pageSize);
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }


    /**
     * Instantiates a new Book service.
     *
     * @param bookRepository     the bookRepository
     * @param bookCopyRepository
     */
    @Autowired
    public BookService(BookRepository bookRepository, PublisherRepository publisherRepository, BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    /**
     * Search book by name.
     *
     * @param pageable the pageable
     * @param names    the names
     * @return the page
     */
    public Page<Book> searchBookByName(Pageable pageable, String[] names) {
        List<Book> result = new ArrayList<>();
        for (String name :
                names) {
            result.addAll(this.bookRepository.getBooksByTitleContains(name));
        }
        return this.ListToPage(pageable, result);
    }


    /**
     * @param pageable the pageable
     * @param name     search terms
     * @return page instance contains all book fit the search term
     */
    public Page<Book> searchBookByFullName(Pageable pageable, String name) {
        return this.bookRepository.getBooksByTitle(pageable, name);
    }

    /**
     * @param pageable the pageable
     * @param pinyins  pinyin array
     * @return page instance contains all books fit the search terms
     */
    public Page<Book> searchBookByPinyin(Pageable pageable, String[] pinyins) {
        List<Book> result = new ArrayList<>();
        for (String pinyin : pinyins) {
            result.addAll(this.bookRepository.getBooksByTitlePinyin(pinyin));
        }
        return this.ListToPage(pageable, result);

    }

    public Page<Book> searchBookByAuthor(Pageable pageable, String author) {
        return bookRepository.getBookByAuthor(pageable, author);
    }


    /**
     * Search for all books contains the requested author
     *
     * @param pageable pageable
     * @param authors  author array
     * @return page
     */
    public Page<Book> searchBookByAuthors(Pageable pageable, String[] authors) {
        Set<Book> result = new HashSet<>();
        for (int i = 0; i < authors.length; i++) {
            List<Book> books = bookRepository.getBookByAuthor(authors[i]);
            for (Book book : books) {
                boolean flag = true;
                for (int j = 0; j < authors.length; j++) {
                    if (j != i) {
                        List<String> bookAuthors = book.getAuthor();
                        if (!bookAuthors.contains(authors[j])) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    result.add(book);
                }
            }
        }
        return ListToPage(pageable, new ArrayList<>(result));
    }

    public Page<Book> searchBookByFullAuthors(Pageable pageable, String[] authors) {
        return bookRepository.getBookByMatchAuthorExactly(pageable, Arrays.asList(authors));
    }

    public Page<Book> searchBookByPublisher(Pageable pageable, String publisherName) {
        return bookRepository.getBookByPublisher(pageable, publisherRepository.findPublisherByName(publisherName));
    }

    public Page<Book> search(Specification<Book> specification, Pageable pageable) {
        return this.bookRepository.findAll(specification, pageable);
    }

    @Override
    public List<Book> add(Iterable<Book> books) {
        return this.bookRepository.save(books);
    }

    @Override
    public Book add(Book book) {
        return this.bookRepository.save(book);
    }

    @Override
    public Book get(Long isbn) {
        return this.bookRepository.findOne(isbn);
    }

    @Override
    public List<Book> get(Iterable<Long> longs) {
        return this.bookRepository.findAll(longs);
    }

    @Override
    public Book update(Book book) throws ResourceNotFoundByIdException {
        Book _book = this.bookRepository.findOne(book.getIsbn());
        if (_book == null) {
            throw new ResourceNotFoundByIdException();
        }
        return this.bookRepository.save(book);
    }

    @Override
    @Transactional
    public List<Book> update(Iterable<Book> books) throws ResourceNotFoundByIdException {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            result.add(this.update(book));
        }
        return result;
    }

    @Override
    public void delete(Book book) throws ResourceNotFoundByIdException, ResourceNotDeletable {
        List<BookCopy> notDeletableCopy = this.bookCopyRepository.isNotDeletable(book.getIsbn());
        if (notDeletableCopy.size() > 0) {
            //if there is some copy that cannot be delete
            throw new ResourceNotDeletable(notDeletableCopy);
        }
        this.bookRepository.delete(book);
    }

    @Override
    public void delete(Iterable<Book> books) throws ResourceNotDeletable {
        List<Book> notDeletableBooks = StreamSupport.stream(books.spliterator(), false).filter(book -> !this.bookCopyRepository.isNotDeletable(book.getIsbn()).isEmpty()).collect(Collectors.toList());
        if (notDeletableBooks.size() > 0) {
            throw new ResourceNotDeletable(notDeletableBooks);
        }
        this.bookRepository.delete(books); // will delete correspond copy
    }

}
