package org.example.custom2;

import java.util.List;

/**
 * @describer:
 * @author:fanzhihui
 * @Date:2019/6/19
 * @Time:9:40
 */
public class Department {
    public final int deptno;
    public final String name;

    @org.apache.calcite.adapter.java.Array(component = Employee.class)
    public final List<Employee> employees;

    public Department(int deptno, String name, List<Employee> employees) {
        this.deptno = deptno;
        this.name = name;
        this.employees = employees;
    }
    @Override
    public String toString() {
        return "Department{" +
                "deptno=" + deptno +
                ", name='" + name + '\'' +
                ", employees=" + employees +
                '}';
    }
}