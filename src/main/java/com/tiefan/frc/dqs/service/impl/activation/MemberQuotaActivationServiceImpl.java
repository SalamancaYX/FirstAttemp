package com.tiefan.frc.dqs.service.impl.activation;

import com.tiefan.frc.dqs.dao.IMemberQuotaActivationDao;
import com.tiefan.frc.dqs.domain.MemberQuotaActivation;
import com.tiefan.frc.dqs.dto.activation.QueryActivationResp;
import com.tiefan.frc.dqs.service.activation.IMemberQuotaActivationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by jiafeilu on 2018/6/12.
 */
@Service
public class MemberQuotaActivationServiceImpl implements IMemberQuotaActivationService{
    @Resource
    private IMemberQuotaActivationDao memberQuotaActivationDao;

    @Override
    public QueryActivationResp queryByMemberId(String memberId) {
        MemberQuotaActivation memberQuotaActivation = memberQuotaActivationDao.queryByMemberId(memberId);
        if (memberQuotaActivation != null) {
            QueryActivationResp resp = new QueryActivationResp();
            resp.setMemberId(memberId);
            resp.setRefused(memberQuotaActivation.getStatus());
            resp.setRefuseReason(memberQuotaActivation.getRefuseReason());
            resp.setCreditLevel(memberQuotaActivation.getCreditLevel());
            resp.setTotalQuota(memberQuotaActivation.getTotalQuota());

            return resp;
        }

        return null;
    }
}
