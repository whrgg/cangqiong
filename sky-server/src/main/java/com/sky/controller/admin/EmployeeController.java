package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    @PostMapping
    public Result<String> login(@RequestBody EmployeeDTO employeeDTO){

        employeeService.save(employeeDTO);



        return Result.success("新增成功");
    }

    @GetMapping("/page")
    public Result<PageResult> getpage(EmployeePageQueryDTO employeePageQueryDTO){

        PageResult pageResult=employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(Integer id,@PathVariable Integer status){

        employeeService.updateStatus(id,status);

        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Integer id){

        Employee employee=employeeService.getById(id);

        return Result.success(employee);
    }

    @PutMapping()
    public Result<String> updateEmployee(@RequestBody EmployeeDTO employeeDTO){




        employeeService.updateEmployeeById(employeeDTO);

        return Result.success("更新成功");
    }
}
