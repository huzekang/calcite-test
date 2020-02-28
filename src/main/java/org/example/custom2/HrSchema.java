package org.example.custom2;

import java.util.Arrays;
import java.util.Collections;

/**
 * @describer: HrSchema定义了两个表
 */
public class HrSchema {
    public final Employee[] emps;
    public final Department[] depts;

    /**
     * 解析下面代码：
     * 构造函数初始化赋值
     */
    public HrSchema(){
        this.emps=new Employee[]{
                new Employee(100,10,"Bill",10000,1000),
                new Employee(200,10,"Bill",10000,1000),
                new Employee(400,10,"Bill",10000,1000),
                new Employee(300,20,"Bill",10000,null),
        };
        this.depts=new Department[]{
                new Department(10,"Sales", Arrays.asList(emps[0],emps[2])),
                new Department(30,"Marking", Arrays.asList(emps[0],emps[2])),
//                    new Department(30,"Marking", ImmutableIntList.of()),
                new Department(40,"Sales", Collections.singletonList(emps[1])),
        };
    }

}