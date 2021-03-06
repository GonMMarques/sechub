// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.job;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.daimler.sechub.domain.administration.user.User;
import com.daimler.sechub.domain.administration.user.UserRepository;
import com.daimler.sechub.sharedkernel.SecHubEnvironment;
import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.logging.AuditLogService;
import com.daimler.sechub.sharedkernel.messaging.DomainMessage;
import com.daimler.sechub.sharedkernel.messaging.DomainMessageFactory;
import com.daimler.sechub.sharedkernel.messaging.DomainMessageService;
import com.daimler.sechub.sharedkernel.messaging.IsSendingAsyncMessage;
import com.daimler.sechub.sharedkernel.messaging.JobMessage;
import com.daimler.sechub.sharedkernel.messaging.MessageDataKeys;
import com.daimler.sechub.sharedkernel.messaging.MessageID;
import com.daimler.sechub.sharedkernel.usecases.job.UseCaseAdministratorRestartsJob;
import com.daimler.sechub.sharedkernel.usecases.job.UseCaseAdministratorRestartsJobHard;
import com.daimler.sechub.sharedkernel.validation.UserInputAssertion;

@Service
public class JobRestartRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(JobRestartRequestService.class);

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    UserInputAssertion assertion;

    @Autowired
    DomainMessageService eventBusService;

    @Autowired
    JobInformationRepository repository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    SecHubEnvironment sechubEnvironment;

    @Validated
    @UseCaseAdministratorRestartsJob(@Step(number = 2, name = "Restart job", description = "Will trigger event that job restart (soft) requested"))
    public void restartJob(UUID jobUUID) {
        assertion.isValidJobUUID(jobUUID);

        auditLogService.log("Requested restart (soft) of job {}", jobUUID);

        JobMessage message = buildMessage(jobUUID);

        /* trigger event */
        triggerJobRestartRequest(message);
    }

    @Validated
    @UseCaseAdministratorRestartsJobHard(@Step(number = 2, name = "Restart job", description = "Will trigger event that job restart (hard) requested"))
    public void restartJobHard(UUID jobUUID) {
        assertion.isValidJobUUID(jobUUID);

        auditLogService.log("Requested restart (hard) of job {}", jobUUID);

        JobMessage message = buildMessage(jobUUID);

        /* trigger event */
        triggerJobHardRestartRequest(message);
    }

    private JobMessage buildMessage(UUID jobUUID) {
        JobMessage message = new JobMessage();

        message.setJobUUID(jobUUID);

        JobInformation probe = new JobInformation();
        probe.setJobUUID(jobUUID);
        Example<JobInformation> example = Example.of(probe);
        Optional<JobInformation> optJobInfo = repository.findOne(example);
        if (!optJobInfo.isPresent()) {
            LOG.warn("Did not found job information, so not able to resolve owner email address");
            return message;
        }

        JobInformation jobInfo = optJobInfo.get();
        if (jobInfo.owner == null) {
            LOG.warn("Did not found owner inside job information, so not able to resolve owner email address");
            return message;
        }
        Optional<User> optUser = userRepository.findById(jobInfo.owner);
        if (!optUser.isPresent()) {
            LOG.warn("Did not found owner {} inside user repo, so not able to resolve owner email address", jobInfo.owner);
            return message;
        }
        message.setOwner(jobInfo.owner);
        message.setOwnerEmailAddress(optUser.get().getEmailAdress());
        return message;
    }

    @IsSendingAsyncMessage(MessageID.REQUEST_JOB_RESTART)
    private void triggerJobRestartRequest(JobMessage message) {

        DomainMessage infoRequest = DomainMessageFactory.createEmptyRequest(MessageID.REQUEST_JOB_RESTART);
        infoRequest.set(MessageDataKeys.JOB_RESTART_DATA, message);
        infoRequest.set(MessageDataKeys.ENVIRONMENT_BASE_URL,sechubEnvironment.getServerBaseUrl());

        eventBusService.sendAsynchron(infoRequest);
    }
    
    @IsSendingAsyncMessage(MessageID.REQUEST_JOB_RESTART_HARD)
    private void triggerJobHardRestartRequest(JobMessage message) {

        DomainMessage infoRequest = DomainMessageFactory.createEmptyRequest(MessageID.REQUEST_JOB_RESTART_HARD);
        infoRequest.set(MessageDataKeys.JOB_RESTART_DATA, message);
        infoRequest.set(MessageDataKeys.ENVIRONMENT_BASE_URL,sechubEnvironment.getServerBaseUrl());

        eventBusService.sendAsynchron(infoRequest);
    }

}
