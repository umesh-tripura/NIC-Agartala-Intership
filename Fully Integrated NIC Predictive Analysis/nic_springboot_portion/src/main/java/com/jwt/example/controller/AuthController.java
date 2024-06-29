package com.jwt.example.controller;

import com.jwt.example.entities.User;
import com.jwt.example.models.JwtRequest;
import com.jwt.example.models.JwtResponse;
import com.jwt.example.models.Nic;
import com.jwt.example.security.JwtHelper;
import com.jwt.example.services.NicService;
import com.jwt.example.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private NicService nicService;

    @Autowired
    private JwtHelper helper;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }


     @PostMapping("/create-user")
    public User createUser (@RequestBody User user){

   return userService.createUser(user);
    }

    

    @PostMapping("/nic")
    public ResponseEntity<String> saveNic(@RequestBody Nic nic){
        boolean result = nicService.saveNic(nic);
        if(result)
            return ResponseEntity.ok("Data Pushed To Redis");
        else
            return ResponseEntity.status(HttpStatus.valueOf(0)).build();
    }

    @GetMapping("/fetch-nic")
    public ResponseEntity<List<Nic>> fetchAllUser(HttpServletResponse response) throws IOException {
        List<Nic> nics = nicService.fetchAllUser();
       
        // String filename = "Nic.csv";
        
        // Path fileLocation = Paths.get(System.getProperty("user.dir"), "../..", filename).normalize();
        
        // ICsvBeanWriter csvWriter = new CsvBeanWriter(new FileWriter(fileLocation.toString()), CsvPreference.STANDARD_PREFERENCE);
        
        // String[] csvHeader = {"Key", "Category", "Sub_category", "Expected_period_in_month", "Out_data_pattern", "Demand_date", "Demand_qty", "Analysis_year"};
        // String[] nameMapping = {"key", "category", "sub_category", "expected_period_in_month", "out_data_pattern", "demand_date", "demand_qty", "analysis_year"};
       
        // csvWriter.writeHeader(csvHeader);
        // for (Nic nic : nics) {
        //     csvWriter.write(nic, nameMapping);
        // }
       
        // csvWriter.close();
       
        nicService.deleteAllUser();
       
        // Resource fileResource = new UrlResource(fileLocation.toUri());
      
        // String contentType = "text/csv";
        
        // response.setContentType(contentType);
        // response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
       
        // return ResponseEntity.ok()
        //         .contentType(MediaType.parseMediaType(contentType))
        //         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        //         .body(fileResource);
        return ResponseEntity.ok(nics);
    }

}
