package com.hmall.trade.controller;

import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.common.utils.BeanUtils;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.OrderDetail;
import com.hmall.trade.domain.vo.OrderVO;
import com.hmall.trade.service.IOrderDetailService;
import com.hmall.trade.service.IOrderService;

import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;

    @ApiOperation("根据id查询订单")
    @GetMapping("{id}")
    public OrderVO queryOrderById(@Param ("订单id")@PathVariable("id") Long orderId) {
        return BeanUtils.copyBean(orderService.getById(orderId), OrderVO.class);
    }

    @ApiOperation("创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }

    @ApiOperation("根据订单id查询订单详情")
    @GetMapping("/{orderId}/details")
    public List<OrderDetailDTO> queryOrderDetailsByOrderId(@PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.lambdaQuery()
                .eq(OrderDetail::getOrderId, orderId)
                .list();
        return BeanUtils.copyList(orderDetails, OrderDetailDTO.class);
    }
}
