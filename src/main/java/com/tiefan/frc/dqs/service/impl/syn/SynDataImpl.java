/**
 *
 */
package com.tiefan.frc.dqs.service.impl.syn;


import com.tiefan.frc.dqs.dao.lrr.IUserAddressbookDao;
import com.tiefan.frc.dqs.dao.lrr.IUserAppListDao;
import com.tiefan.frc.dqs.dao.lrr.IUserFavListDao;
import com.tiefan.frc.dqs.dao.lrr.IUserSmsListDao;
import com.tiefan.frc.dqs.domain.lrr.UserAppSmsFavList;
import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.dto.syn.ContractsSyn;
import com.tiefan.frc.dqs.dto.syn.UserApplistsDto;
import com.tiefan.frc.dqs.dto.syn.UserFavoritesDto;
import com.tiefan.frc.dqs.dto.syn.UserSms;
import com.tiefan.frc.dqs.service.channel.ICssChannelService;
import com.tiefan.frc.dqs.service.oas.IOasService;
import com.tiefan.frc.dqs.service.syn.ISynDataService;
import com.tiefan.frc.dqs.support.constants.DqsConstant;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import com.tiefan.frc.dqs.support.utils.MD5;
import com.tiefan.frc.dqs.support.utils.http.FSPClientUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步数据
 */
@Service
public class SynDataImpl implements ISynDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynDataImpl.class);

    @Resource
    private IUserAppListDao appListDao;
    @Resource
    private IUserSmsListDao smsListDao;
    @Resource
    private IUserAddressbookDao addressbookDao;
    @Resource
    private IUserFavListDao favListDao;
    @Resource
    private IOasService oasService;
    @Resource
    private ICssChannelService cssChannelService;
    @Resource
    private FSPClientUtil fspUtil;

    @Override
    public ResponseVo saveContracts(ContractsSyn contracts) {
        ResponseVo responseVo = new ResponseVo();
        int count = 0;
        try {
            if (contracts != null && StringUtils.isNotBlank(contracts.getMemberId())) {
                List<String> md5List = addressbookDao.selectMd5ByMemberId(contracts.getMemberId());
                String content = JsonUtil.filterEmoji(JsonUtil.toString(contracts.getContracts()));
                String nowMd5 = MD5.sign(content, content, "UTF-8");
                boolean insert = true;
                if (CollectionUtils.isNotEmpty(md5List)) {
                    for (String oldMd5 : md5List) {
                        if (oldMd5.equalsIgnoreCase(nowMd5)) {
                            insert = false;
                            break;
                        }
                    }
                }
                if (insert) {
                    UserAppSmsFavList apps = new UserAppSmsFavList(contracts.getMemberId(), contracts.getAccount(), contracts.getAppType(), content, nowMd5);
                    count = addressbookDao.insert(apps);
                    cssChannelService.addresslistNotify(contracts.getMemberId(), contracts.getAccount());
                } else {
                    LOGGER.info("addressbook列表相同，不保存,{}", contracts.getMemberId());
                }

            }
            responseVo.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("保存addressbook列表异常,{}", JsonUtil.toString(contracts));
            LOGGER.error("保存addressbook列表异常", e);
            responseVo.setSuccess(false);
        }
        responseVo.setData(count);
        return responseVo;

    }

    @Override
    public ResponseVo saveUserApplist(UserApplistsDto applist) {
        ResponseVo responseVo = new ResponseVo();
        int count = 0;
        try {
            if (applist != null && StringUtils.isNotBlank(applist.getMemberId())) {
                List<String> md5List = appListDao.selectMd5ByMemberId(applist.getMemberId());
                String content = JsonUtil.filterEmoji(JsonUtil.toString(applist.getApplistDetail()));
                String nowMd5 = MD5.sign(content, content, DqsConstant.MD5_ENCODE);
                boolean insert = true;
                if (CollectionUtils.isNotEmpty(md5List)) {
                    for (String oldMd5 : md5List) {
                        if (oldMd5.equalsIgnoreCase(nowMd5)) {
                            insert = false;
                            break;
                        }
                    }
                }
                if (insert) {
                    UserAppSmsFavList apps = new UserAppSmsFavList(applist.getMemberId(), applist.getAccount(), applist.getAppType(), content, nowMd5);
                    count = appListDao.insert(apps);
                } else {
                    LOGGER.info("app列表相同，不保存,{}", applist.getMemberId());
                }

            }
            responseVo.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("保存APP列表异常,{}", JsonUtil.toString(applist));
            LOGGER.error("保存APP列表异常", e);
            responseVo.setSuccess(false);
        }
        responseVo.setData(count);
        return responseVo;
    }

    @Override
    public ResponseVo saveUserFavorites(UserFavoritesDto dto) {
        ResponseVo responseVo = new ResponseVo();
        int count = 0;
        try {
            if (dto != null && StringUtils.isNotBlank(dto.getMemberId())) {
                List<String> md5List = favListDao.selectMd5ByMemberId(dto.getMemberId());
                String content = JsonUtil.filterEmoji(JsonUtil.toString(dto.getBookMarkDetail()));
                String nowMd5 = MD5.sign(content, content, DqsConstant.MD5_ENCODE);
                boolean insert = true;
                if (CollectionUtils.isNotEmpty(md5List)) {
                    for (String oldMd5 : md5List) {
                        if (oldMd5.equalsIgnoreCase(nowMd5)) {
                            insert = false;
                            break;
                        }
                    }
                }
                if (insert) {
                    UserAppSmsFavList apps = new UserAppSmsFavList(dto.getMemberId(), dto.getAccount(), dto.getAppType(), content, nowMd5);
                    count = favListDao.insert(apps);
                } else {
                    LOGGER.info("fav列表相同，不保存,{}", dto.getMemberId());
                }

            }
            responseVo.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("保存fav列表异常,{}", JsonUtil.toString(dto));
            LOGGER.error("保存fav列表异常", e);
            responseVo.setSuccess(false);
        }
        responseVo.setData(count);
        return responseVo;
    }

    @Override
    public ResponseVo saveUserSmslist(UserSms userSms) {
        ResponseVo responseVo = new ResponseVo();
        int count = 0;
        try {
            if (userSms != null && StringUtils.isNotBlank(userSms.getMemberId())) {
                List<String> md5List = smsListDao.selectMd5ByMemberId(userSms.getMemberId());
                String content = JsonUtil.filterEmoji(JsonUtil.toString(userSms.getSmsListDetail()));
                String nowMd5 = MD5.sign(content, content, DqsConstant.MD5_ENCODE);

                oasService.sendSmsGamble(userSms, nowMd5);

                boolean insert = true;
                if (CollectionUtils.isNotEmpty(md5List)) {
                    for (String oldMd5 : md5List) {
                        if (oldMd5.equalsIgnoreCase(nowMd5)) {
                            insert = false;
                            break;
                        }
                    }
                }
                if (insert) {
                    UserAppSmsFavList apps = new UserAppSmsFavList(userSms.getMemberId(), userSms.getAccount(), userSms.getAppType(), content, nowMd5);
                    count = smsListDao.insert(apps);
                    Map paramMap = new HashMap<>();
                    paramMap.put("memberId",userSms.getMemberId());
                    //通知xhy计算短信统计规则项
                    fspUtil.invokeFspService("FRC.XHY.NotifySmsUpdateController.smsListUpdate", JsonUtil.toString(paramMap), 2000, true, false);
                } else {
                    LOGGER.info("sms列表相同，不保存,{}", userSms.getMemberId());
                }

            }
            responseVo.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("保存sms列表异常,{}", JsonUtil.toString(userSms));
            LOGGER.error("保存sms列表异常", e);
            responseVo.setSuccess(false);
        }
        responseVo.setData(count);
        return responseVo;
    }
}
