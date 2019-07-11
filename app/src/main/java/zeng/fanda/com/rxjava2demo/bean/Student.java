package zeng.fanda.com.rxjava2demo.bean;

import java.util.List;

/**
 * @author 曾凡达
 * @date 2019/7/9
 */
public class Student {
    public List<Course> mCourses;
    public String name;
    public int age;

    public Student() {
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
