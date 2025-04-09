package vn.hoidanit.jobhunter.service.googleService;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vn.hoidanit.jobhunter.domain.User;

@Service
public class ApiService {
    private final String API_URL = "https://jsonplaceholder.typicode.com/users";
    private RestTemplate restTemplate;

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getUsers() {
        User[] users = restTemplate.getForObject(API_URL, User[].class);
        return Arrays.asList(users);
    }
}
