package com.hmall.search.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.client.TradeClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.search.domain.po.Item;
import com.hmall.search.mapper.ItemMapper;
import com.hmall.search.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    private final TradeClient tradeClient;

    @Override
    @Transactional
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.hmall.item.mapper.ItemMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("更新库存异常，可能是库存不足!", e);
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
    }

    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }

    @Override
    public void restoreStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.hmall.item.mapper.ItemMapper.restoreStock";
        executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        return;

    }

    @Override
    @Transactional
    public void addStock(Long orderId) {
        List<OrderDetailDTO> orderDetails = tradeClient.queryOrderDetailsByOrderId(orderId);
        restoreStock(orderDetails);
    }
}
