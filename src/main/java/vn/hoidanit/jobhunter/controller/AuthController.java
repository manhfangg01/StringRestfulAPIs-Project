package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.ObjectCollapsed;
import vn.hoidanit.jobhunter.util.error.ObjectNotExisted;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${hoidanit.jwt.refresh_token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResUserDTO> register(@Valid @RequestBody User postManUser) throws ObjectCollapsed {
        if (this.userService.checkExistedEmail(postManUser.getEmail())) {
            throw new ObjectCollapsed("Tài khoản này đã tồn tại");
        }
        String hashPassword = passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(postManUser));

    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        User user = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        // 2. Debug chi tiết
        System.out.println("\n=== DEBUG PASSWORD ===");
        System.out.println("DB Password: " + user.getPassword());
        System.out.println("Input Password (raw): " + loginDTO.getPassword());

        // 3. Kiểm tra mật khẩu BẰNG PHƯƠNG PHÁP ĐÚNG
        boolean isMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        System.out.println("Password Match Result: " + isMatch);

        if (!isMatch) {
            // 4. Debug thêm: Tạo hash mới để so sánh thủ công
            String newHash = passwordEncoder.encode("123456");
            System.out.println("New Hash of '123456': " + newHash);
            System.out.println("Compare with DB Hash: " + user.getPassword().equals(newHash));

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(),
                    currentUserDB.getEmail(), currentUserDB.getName(), currentUserDB.getRole());
            res.setUser(userLogin);
        }
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(accessToken);

        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        // update user
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // Set Cookie
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                // .domain("example.com")
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());
            userGetAccount.setUser(userLogin);

        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("get user by refresh Token")
    public ResponseEntity<ResLoginDTO> getRefreshToeken(@CookieValue(name = "refresh_token") String refreshToken)
            throws IdInvalidException, ObjectNotExisted {

        // Check existed refreshToken
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IdInvalidException("Không tồn tại refreshToken");
        }
        // check Valid refreshToken
        Jwt jwt = securityUtil.checkValidRefreshToken(refreshToken);

        // Get data from token after decoding
        String email = jwt.getSubject();
        User user = userService.handleGetUserByUserName(email);
        if (user == null) {
            throw new ObjectNotExisted("Người dùng không tồn tại");
        }

        // Compare current refresh token with the one in DB
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IdInvalidException("Invalid refresh token");
        }

        // 5. Tạo RestLoginDTO từ thông tin user
        ResLoginDTO res = new ResLoginDTO();
        if (user != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(),
                    user.getEmail(), user.getName(), user.getRole());
            res.setUser(userLogin);
        }

        // create brand new Tokens
        String newAccessToken = securityUtil.createAccessToken(email, res);
        String newRefreshToken = securityUtil.createRefreshToken(email, res);

        // update new RefreshToken to DB
        this.userService.updateUserToken(newRefreshToken, email);

        // update responseDTO with new AccessToken
        res.setAccessToken(newAccessToken);

        // Set Token vào Cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Log out user")
    public ResponseEntity<Void> logoutUser() throws IdInvalidException {
        // Lấy ra email người dùng
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refreshToke in DB
        this.userService.updateUserToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

}
