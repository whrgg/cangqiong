package com.sky.service;

import com.sky.dto.*;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {


    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}