//package com.business.user_service.service;
//
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Service
//public class UserServiceApiCallService {
//    private static final String FILE_PATH = "user_service_api_call_count.txt";
//
//    public int getApiCallCount() {
//        try {
//            Path path = Paths.get(FILE_PATH);
//            if (Files.exists(path)) {
//                String countStr = new String(Files.readAllBytes(path)).trim();
//                // Log giá trị đọc được từ tệp
//                System.out.println("Read count from file: " + countStr);
//                if (countStr.isEmpty()) {
//                    return 0;
//                }
//                return Integer.parseInt(countStr);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NumberFormatException e) {
//            System.err.println("Invalid number format in the API call count file.");
//        }
//        return 0;
//    }
//
//    public void incrementApiCallCount() {
//        int currentCount = getApiCallCount();
//        currentCount++;
//        System.out.println("Incremented count: " + currentCount); // Log sau khi tăng số
//        writeApiCallCount(currentCount);
//    }
//
//    private void writeApiCallCount(int count) {
//        try {
//            System.out.println("Writing count to file: " + count); // Log trước khi ghi vào tệp
//            Files.write(Paths.get(FILE_PATH), String.valueOf(count).getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
