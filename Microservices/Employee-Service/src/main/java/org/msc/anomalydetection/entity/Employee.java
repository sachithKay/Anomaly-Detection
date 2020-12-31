package org.msc.anomalydetection.entity;

public class Employee {

    private String Name;
    private int salary;
    private String department;
    private String address;

    public String getName() {

        return Name;
    }

    public void setName(String name) {

        Name = name;
    }

    public int getSalary() {

        return salary;
    }

    public void setSalary(int salary) {

        this.salary = salary;
    }

    public String getDepartment() {

        return department;
    }

    public void setDepartment(String department) {

        this.department = department;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }
}
