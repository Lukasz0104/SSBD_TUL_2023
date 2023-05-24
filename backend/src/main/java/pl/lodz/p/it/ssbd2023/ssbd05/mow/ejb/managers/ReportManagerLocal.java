package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.util.List;

@Local
public interface ReportManagerLocal extends CommonManagerInterface {
    Report getReportDetails(Long id) throws AppBaseException;

    List<Report> getAllCommunityReports(Long id) throws AppBaseException;

    Report getCommunityReportByYear(Long year) throws AppBaseException;
}
