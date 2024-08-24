package com.example.springapi.service;

import com.example.springapi.api.dto.CustomerDTO;
import com.example.springapi.api.dto.OrderDTO;
import com.example.springapi.persistence.entity.CustomerEntity;
import com.example.springapi.persistence.repository.CustomerRepository;
import com.example.springapi.publisher.RabbitMQProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private RabbitMQProducer producer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CustomerService(CustomerRepository customerRepository, RabbitMQProducer producer) {
        this.customerRepository = customerRepository;
        this.producer = producer;
    }

    public Optional<CustomerDTO> getCustomer(Integer id) {
        Optional<CustomerEntity> customerFromDB = customerRepository.getCustomerEntityById(id);

        return customerFromDB.map(this::customerEntityMapper);
    }

    public List<CustomerDTO> getCustomers() {
        List<CustomerEntity> customersFromDB = customerRepository.findAll().stream().limit(10).collect(Collectors.toList());

        return customersFromDB.stream().map(this::customerEntityMapper).collect(Collectors.toList());
    }

    public void postCustomer(CustomerDTO customerDTO) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(generateId()); // Générer un nouvel identifiant
        customerEntity.setName(customerDTO.getName());
        customerEntity.setAge(customerDTO.getAge());
        customerEntity.setEmail(customerDTO.getEmail());
        customerRepository.save(customerEntity);
    }

    public void putCustomer(Integer id, CustomerDTO customerDTO) {
        Optional<CustomerEntity> optionalCustomerEntity = customerRepository.getCustomerEntityById(id);
        optionalCustomerEntity.ifPresent(customerEntity -> {
            customerEntity.setName(customerDTO.getName());
            customerEntity.setAge(customerDTO.getAge());
            customerEntity.setEmail(customerDTO.getEmail());
            customerRepository.save(customerEntity);
        });
    }

    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }

    private CustomerDTO customerEntityMapper(CustomerEntity customerEntity){
        return CustomerDTO.builder()
                .name(customerEntity.getName())
                .age(customerEntity.getAge())
                .email(customerEntity.getEmail())
                .build();
    }

    private int generateId() {
        // Implémentez la logique pour générer un identifiant unique, par exemple en utilisant un compteur ou une méthode de génération d'identifiant unique
        // Pour cet exemple, vous pouvez simplement utiliser un identifiant incrémental
        return Math.toIntExact(customerRepository.count() + 1);
    }

    public List<OrderDTO> getMyOrders(int id) throws JsonProcessingException {
        String reply = producer.sendMessageWithReturn("ACTION_GET_MY_ORDERS:"+id);
        if (reply == "No reply received"){
            return null;
        }
        System.out.println("ERREURRRRRRRRRRRRR+++++++++++++++++++++++++");
        List<OrderDTO> orderDTO = objectMapper.readValue(reply, new TypeReference<List<OrderDTO>>(){});
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
        return orderDTO;
    }
}
