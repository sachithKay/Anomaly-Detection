package org.msc.anomalydetection;

import org.msc.anomalydetection.entity.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class EmployeeServiceController {

    @RequestMapping("/index")
    @ResponseBody
    public String index() {

        return "success";
    }

    @GetMapping("/getEmployees")
    @ResponseBody
    public List<Employee> getEmployees() {

        List<Employee> employeeList = generatePseudoEmployees();
        return employeeList;
    }

    @GetMapping("/getEmployees/{id}")
    @ResponseBody
    public Employee getEmployees(@PathVariable String id) {

        Employee employee = new Employee();
        employee.setName("john");
        employee.setDepartment("HR");
        employee.setSalary(2500);
        employee.setAddress("No.20, \nLondon");
        return employee;
    }

    private List<Employee> generatePseudoEmployees() {

        List<Employee> employeeList = new ArrayList<Employee>();
        for (int i = 0; i < 5; i++) {
            Employee employee = new Employee();
            employee.setName(String.join(" ", generateRandomWords(2)));
            employee.setDepartment(String.join("", generateRandomWords(1)));
            employeeList.add(employee);
        }
        return employeeList;
    }

    private String[] generateRandomWords(int numberOfWords) {

        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for (int i = 0; i < numberOfWords; i++) {
            char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for (int j = 0; j < word.length; j++) {
                word[j] = (char) ('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }
}
