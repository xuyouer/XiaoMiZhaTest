package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserVipInfo;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_vip_info(用户会员信息表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserVipInfoService extends IService<UserVipInfo> {

    /**
     * 分页获取用户VIP信息列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户VIP信息列表
     */
    Page<UserVipInfo> getVipInfosPage(Integer current, Integer pageSize);

    /**
     * 根据VIP ID获取用户VIP信息详情
     *
     * @param vipId VIP ID
     * @return 用户VIP信息详情
     */
    UserVipInfo getVipInfoById(Integer vipId);

    /**
     * 新增用户VIP信息
     *
     * @param userVipInfo 用户VIP信息
     * @return 是否新增成功
     */
    boolean addVipInfo(UserVipInfo userVipInfo);

    /**
     * 更新用户VIP信息
     *
     * @param vipId       VIP ID
     * @param userVipInfo 用户VIP信息
     * @return 是否更新成功
     */
    boolean updateVipInfo(Integer vipId, UserVipInfo userVipInfo);

    /**
     * 删除用户VIP信息
     *
     * @param vipId VIP ID
     * @return 是否删除成功
     */
    boolean deleteVipInfo(Integer vipId);

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

    /**
     * 批量添加用户VIP信息
     *
     * @param userVipInfoList 用户VIP信息列表
     * @return 是否添加成功
     */
    boolean batchAddVipInfos(List<UserVipInfo> userVipInfoList);

    /**
     * 批量更新用户VIP信息
     *
     * @param userVipInfoList 用户VIP信息列表
     * @return 是否更新成功
     */
    boolean batchUpdateVipInfos(List<UserVipInfo> userVipInfoList);

    /**
     * 批量删除用户VIP信息
     *
     * @param vipIds VIP ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteVipInfos(List<Integer> vipIds);

}
