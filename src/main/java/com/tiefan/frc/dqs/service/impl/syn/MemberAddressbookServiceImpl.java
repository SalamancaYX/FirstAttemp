package com.tiefan.frc.dqs.service.impl.syn;

import com.tiefan.frc.dqs.dao.lrr.IUserAddressbookDao;
import com.tiefan.frc.dqs.dao.lrr.IUserCallinfoDao;
import com.tiefan.frc.dqs.domain.lrr.UserAppSmsFavList;
import com.tiefan.frc.dqs.service.syn.IMemberAddressbookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MemberAddressbookServiceImpl implements IMemberAddressbookService {
    @Resource
    private IUserAddressbookDao addressbookDao;
    @Resource
    private IUserCallinfoDao callinfoDao;
    @Override
    public UserAppSmsFavList getAddressbookByMemberId(String memberId) {
        return addressbookDao.getAddressbookByMemberId(memberId);
    }

    @Override
    public UserAppSmsFavList getCallinfoByMemberId(String memberId) {
        return callinfoDao.getCallinfoByMemberId(memberId);
    }
}
