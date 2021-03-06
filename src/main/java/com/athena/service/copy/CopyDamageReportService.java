package com.athena.service.copy;

import com.athena.exception.http.IllegalEntityAttributeException;
import com.athena.exception.http.InvalidCopyTypeException;
import com.athena.exception.http.ResourceNotDeletable;
import com.athena.exception.http.ResourceNotFoundByIdException;
import com.athena.model.borrow.Borrow;
import com.athena.model.copy.CopyDamageReport;
import com.athena.repository.mongo.CopyDamageReportRepository;
import com.athena.service.ModelCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Tommy on 2018/2/12.
 */
@Service
public class CopyDamageReportService implements ModelCRUDService<CopyDamageReport, String> {

    private final CopyDamageReportRepository copyDamageReportRepository;

    @Autowired
    public CopyDamageReportService(CopyDamageReportRepository copyDamageReportRepository) {
        this.copyDamageReportRepository = copyDamageReportRepository;
    }


    /**
     * Create copy damage report with handler info and borrow info.
     *
     *
     *
     * @param handlerId       the handler id
     * @param lastKnownBorrow the last known borrow
     * @return the copy damage report
     */
    public CopyDamageReport create(Long handlerId, Borrow lastKnownBorrow) {
        CopyDamageReport copyDamageReport = new CopyDamageReport();
        copyDamageReport.setId(UUID.randomUUID());
        copyDamageReport.setHandlerId(handlerId);
        copyDamageReport.setLastKnownBorrow(lastKnownBorrow);

        return copyDamageReport;
    }

    @Override
    public CopyDamageReport add(CopyDamageReport copyDamageReport) {
        return this.copyDamageReportRepository.save(copyDamageReport);
    }

    public CopyDamageReport addImage(CopyDamageReport copyDamageReport, MultipartFile multipartFile) throws IOException {
        if (multipartFile.getContentType() == null) {
            throw new InvalidMimeTypeException("null", "cannot determine mime type");
        }
        return this.copyDamageReportRepository.setImageAndSaveCopyDamageReport(copyDamageReport, multipartFile.getInputStream(), MimeType.valueOf(multipartFile.getContentType()));
    }

    @Override
    public CopyDamageReport get(String id) throws ResourceNotFoundByIdException, InvalidCopyTypeException {
        return this.copyDamageReportRepository.findOne(id);
    }

    @Override
    public CopyDamageReport update(CopyDamageReport copyDamageReport) throws ResourceNotFoundByIdException, IllegalEntityAttributeException {
        return this.copyDamageReportRepository.save(copyDamageReport);
    }

    @Override
    public void delete(CopyDamageReport copyDamageReport) throws ResourceNotFoundByIdException, ResourceNotDeletable {
        this.copyDamageReportRepository.delete(copyDamageReport);
    }

    public void delete(String id) {
        this.copyDamageReportRepository.delete(id);
    }
}
