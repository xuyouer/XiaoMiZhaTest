package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.user.entity.UserVipInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author xiaom
 * @description 针对表【user_vip_info(用户会员信息表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserVipInfoService extends IService<UserVipInfo> {

    /**
     * 创建默认用户会员信息
     *
     * @param userId 用户ID
     * @return 是否创建成功
     */
    boolean createDefaultUserVipInfo(Integer userId);

    /**
     * 激活用户会员信息
     *
     * @param userId 用户ID
     * @return 是否激活成功
     */
    boolean activateUserVipInfo(Integer userId);

    /**
     * 根据用户ID获取用户VIP信息
     *
     * @param userId 用户ID
     * @return 用户VIP信息
     */
    UserVipInfo getUserVipInfoByUserId(Integer userId);

}
