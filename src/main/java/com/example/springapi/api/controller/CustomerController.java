package com.example.springapi.api.controller;

import com.example.springapi.api.dto.CustomerDTO;
import com.example.springapi.publisher.RabbitMQProducer;
import com.example.springapi.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private RabbitMQProducer producer;

    @Autowired
    public CustomerController(CustomerService customerService,RabbitMQProducer producer){
        this.customerService = customerService;
        this.producer = producer;
    }

    @GetMapping("/publish")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message){
        producer.sendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ ...");
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable int id){
        Optional<CustomerDTO> customer = customerService.getCustomer(id);

        return customer.map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getCustomers(){
        List<CustomerDTO> customers = customerService.getCustomers();

        if (!customers.isEmpty()) {
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Void> postCustomer(@RequestBody CustomerDTO customerDTO) {
        customerService.postCustomer(customerDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> putCustomer(@PathVariable int id, @RequestBody CustomerDTO customerDTO) {
        customerService.putCustomer(id, customerDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {
        customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
