package com.example.demo.service;


import com.example.demo.model.Student;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class MyServiceImp implements MyService {
    @Override
    public List<Object> handlerData() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        System.out.println("Công việc đang chạy trên luồng: " + Thread.currentThread().getName());

        Future<List<Student>> future1 =executorService.submit(()->{
            System.out.println("Công việc đang chạy trên luồng: " + Thread.currentThread().getName());
            List<Student> students = new ArrayList<>();
            students.add(new Student(1L,"hoc sinh 1"));
            students.add(new Student(2L,"hoc sinh 2"));
            students.add(new Student(3L,"hoc sinh 3"));
            students.add(new Student(4L,"hoc sinh 4"));
            return students;
        });
        Future<List<User>> future2 = executorService.submit(()->{
            System.out.println("Công việc đang chạy trên luồng: " + Thread.currentThread().getName());

            List<User> users = new ArrayList<>();
            users.add(new User(1L,"người dùng 1"));
            users.add(new User(2L,"người dùng 2"));
            users.add(new User(3L,"người dùng 3"));
            return users;
        });
        try {
            List<Object> allData = new ArrayList<>();
            allData.addAll(future1.get());
            allData.addAll(future2.get());
            return allData;

        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }finally {
            executorService.shutdown();
        }
    }
}
