package vn.hoidanit.jobhunter.service.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import vn.hoidanit.jobhunter.domain.Company;

public class AuditTrailListener {
    private static Log log = LogFactory.getLog(AuditTrailListener.class);

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(Company company) {
        if (company.getId() == 0) {
            log.info("[company AUDIT] About to add a company");
        } else {
            log.info("[company AUDIT] About to update/delete company: " + company.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterAnyUpdate(Company company) {
        log.info("[company AUDIT] add/update/delete complete for company: " + company.getId());
    }

    @PostLoad
    private void afterLoad(Company company) {
        log.info("[company AUDIT] company loaded from database: " + company.getId());
    }
}